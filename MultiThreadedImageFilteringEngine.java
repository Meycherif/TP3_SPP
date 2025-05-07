package tp3;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class MultiThreadedImageFilteringEngine implements IImageFilteringEngine {

    private BufferedImage currentImage;
    private final int numThreads;
    private final List<WorkerThread> workers;
    private final CyclicBarrier barrier;
    private volatile boolean shutdown = false;

    // Inner class for worker threads
    private class WorkerThread extends Thread {
        private int startRow;
        private int endRow;
        private IFilter filter;
        private BufferedImage inputImage;
        private BufferedImage outputImage;

        public WorkerThread(String name) {
            super(name);
        }

        // Synchronized method to assign work
        public synchronized void setWork(int startRow, int endRow, IFilter filter, BufferedImage inputImage, BufferedImage outputImage) {
            this.startRow = startRow;
            this.endRow = endRow;
            this.filter = filter;
            this.inputImage = inputImage;
            this.outputImage = outputImage;
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted() && !shutdown) {
                    // Wait for work assignment (first synchronization point)
                    barrier.await();

                    if (shutdown || Thread.currentThread().isInterrupted()) break;

                    // Apply filter to assigned rows
                    for (int y = startRow; y < endRow; y++) {
                        for (int x = 0; x < outputImage.getWidth(); x++) {
                            filter.applyFilterAtPoint(x, y, inputImage, outputImage);
                        }
                    }

                    // Wait for all threads to finish (second synchronization point)
                    barrier.await();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Preserve interrupt status
            } catch (BrokenBarrierException e) {
                shutdown = true; // Barrier broken, likely due to shutdown
            }
        }
    }

    public MultiThreadedImageFilteringEngine(int k) {
        if (k <= 0) throw new IllegalArgumentException("Number of threads must be positive");
        this.numThreads = k;
        this.workers = new ArrayList<>(k);
        // CyclicBarrier for k worker threads + 1 main thread
        this.barrier = new CyclicBarrier(k + 1);
        // Create and start worker threads
        for (int i = 0; i < k; i++) {
            WorkerThread worker = new WorkerThread("Worker-" + i);
            workers.add(worker);
            worker.start();
        }
    }

    @Override
    public void loadImage(String inputImage) throws Exception {
        try {
            File inputFile = new File(inputImage);
            currentImage = ImageIO.read(inputFile);
            if (currentImage == null) {
                throw new Exception("Failed to load image: " + inputImage);
            }
        } catch (Exception e) {
            throw new Exception("Error loading image: " + e.getMessage(), e);
        }
    }

    @Override
    public void writeOutPngImage(String outFile) throws Exception {
        if (currentImage == null) {
            throw new Exception("No image to save");
        }
        try {
            File outputFile = new File(outFile);
            ImageIO.write(currentImage, "png", outputFile);
        } catch (Exception e) {
            throw new Exception("Error saving image: " + e.getMessage(), e);
        }
    }

    @Override
    public void setImg(BufferedImage newImg) {
        currentImage = newImg;
    }

    @Override
    public BufferedImage getImg() {
        return currentImage;
    }

    @Override
    public void applyFilter(IFilter someFilter) {
        if (currentImage == null) {
            throw new IllegalStateException("No image loaded to apply filter");
        }

        int height = currentImage.getHeight();
        int width = currentImage.getWidth();
        // Create a single output image for all threads to write to
        BufferedImage outputImage = new BufferedImage(width, height, currentImage.getType());

        // Distribute work: divide rows among threads
        int rowsPerThread = height / numThreads;
        int remainder = height % numThreads;
        int currentRow = 0;

        // Assign work to each thread
        for (int i = 0; i < numThreads; i++) {
            int extraRow = i < remainder ? 1 : 0;
            int startRow = currentRow;
            int endRow = startRow + rowsPerThread + extraRow;
            // Handle edge case: if height < numThreads, some threads get 0 rows
            endRow = Math.min(endRow, height);
            workers.get(i).setWork(startRow, endRow, someFilter, currentImage, outputImage);
            currentRow = endRow;
        }

        try {
            // Unblock worker threads (first synchronization point)
            barrier.await();

            // Wait for all threads to complete (second synchronization point)
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            shutdown();
            throw new RuntimeException("Filter application interrupted", e);
        }

        // Update currentImage with the filtered result
        currentImage = outputImage;
    }

    // Shutdown method to terminate worker threads
    public void shutdown() {
        shutdown = true;
        for (WorkerThread worker : workers) {
            worker.interrupt();
        }
        // Wait for threads to terminate
        for (WorkerThread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        workers.clear();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            shutdown();
        } finally {
            super.finalize();
        }
    }
}