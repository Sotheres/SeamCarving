import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private double[][] energyMatrix;
    private int[][] colorMatrix;

    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Constructor's argument is null");
        }
        colorMatrix = new int[picture.width()][picture.height()];
        for (int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
                colorMatrix[i][j] = picture.getRGB(i, j);
            }
        }
        energyMatrix = new double[picture.width()][picture.height()];
        for (int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
                energyMatrix[i][j] = energy(i, j);
            }
        }
    }

    public Picture picture() {
        Picture pic = new Picture(width(), height());
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                pic.setRGB(i, j, colorMatrix[i][j]);
            }
        }
        return pic;
    }

    public int width() {
        return colorMatrix.length;
    }

    public int height() {
        return colorMatrix[0].length;
    }

    public double energy(int x, int y) {
        if (x < 0 || x > colorMatrix.length - 1 || y < 0 || y > colorMatrix[0].length - 1) {
            throw new IllegalArgumentException("Invalid x/y value");
        }
        if (x == 0 || x == colorMatrix.length - 1 || y == 0 || y == colorMatrix[0].length - 1) {
            return 1000;
        }
        int rgbX1 = colorMatrix[x - 1][y];
        int rx1 = (rgbX1 >> 16) & 0xFF;
        int gx1 = (rgbX1 >> 8) & 0xFF;
        int bx1 = (rgbX1) & 0xFF;
        int rgbX2 = colorMatrix[x + 1][y];
        int rx2 = (rgbX2 >> 16) & 0xFF;
        int gx2 = (rgbX2 >> 8) & 0xFF;
        int bx2 = (rgbX2) & 0xFF;
        int rgbY1 = colorMatrix[x][y - 1];
        int ry1 = (rgbY1 >> 16) & 0xFF;
        int gy1 = (rgbY1 >> 8) & 0xFF;
        int by1 = (rgbY1) & 0xFF;
        int rgbY2 = colorMatrix[x][y + 1];
        int ry2 = (rgbY2 >> 16) & 0xFF;
        int gy2 = (rgbY2 >> 8) & 0xFF;
        int by2 = (rgbY2) & 0xFF;
        double dXsqr = (rx2 - rx1) * (rx2 - rx1) + (gx2 - gx1) * (gx2 - gx1) + (bx2 - bx1) * (bx2 - bx1);
        double dYsqr = (ry2 - ry1) * (ry2 - ry1) + (gy2 - gy1) * (gy2 - gy1) + (by2 - by1) * (by2 - by1);

        return Math.sqrt(dXsqr + dYsqr);
    }

    public int[] findHorizontalSeam() {
        double[][] energyT = transposeEnergy(energyMatrix);
        return new PictureSP(energyT).getSeam();
    }

    private double[][] transposeEnergy(double[][] matrix) {
        double[][] matrixT = new double[matrix[0].length][matrix.length];
        for (int x = 0; x < matrixT.length; x++) {
            for (int y = 0; y < matrixT[0].length; y++) {
                matrixT[x][y] = matrix[y][x];
            }
        }
        return matrixT;
    }

    public int[] findVerticalSeam() {
        return new PictureSP(energyMatrix).getSeam();
    }

    public void removeHorizontalSeam(int[] seam) {
        if (colorMatrix[0].length <= 1) {
            throw new IllegalArgumentException("Cannot remove, height is already 1");
        }
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }

    private void transpose() {
        energyMatrix = transposeEnergy(energyMatrix);
        colorMatrix = transposeColor(colorMatrix);
    }

    private int[][] transposeColor(int[][] matrix) {
        int[][] matrixT = new int[matrix[0].length][matrix.length];
        for (int x = 0; x < matrixT.length; x++) {
            for (int y = 0; y < matrixT[0].length; y++) {
                matrixT[x][y] = matrix[y][x];
            }
        }
        return matrixT;
    }

    public void removeVerticalSeam(int[] seam) {
        if (colorMatrix.length <= 1) {
            throw new IllegalArgumentException("Cannot remove, width is already 1");
        }
        validateSeam(seam);

        int[][] trimmedColorMatrix = new int[colorMatrix.length - 1][colorMatrix[0].length];
        for (int i = 0; i < colorMatrix[0].length; i++) {
            for (int j = 0; j < seam[i]; j++) {
                trimmedColorMatrix[j][i] = colorMatrix[j][i];
            }
            for (int j = seam[i] + 1; j < colorMatrix.length; j++) {
                trimmedColorMatrix[j - 1][i] = colorMatrix[j][i];
            }
        }
        colorMatrix = trimmedColorMatrix;

        double[][] trimmedEnergyMatrix = new double[energyMatrix.length - 1][energyMatrix[0].length];
        for (int i = 0; i < energyMatrix[0].length; i++) {
            if (i > 0 && i < energyMatrix[0].length - 1) {
                if (seam[i] == 0) {
                    energyMatrix[seam[i] + 1][i] = energyMatrix[seam[i]][i];
                } else if (seam[i] > 0 && seam[i] < energyMatrix.length - 1) {
                    energyMatrix[seam[i] - 1][i] = energy(seam[i] - 1, i);
                    energyMatrix[seam[i] + 1][i] = energy(seam[i], i);
                } else if (seam[i] == energyMatrix.length - 1) {
                    energyMatrix[seam[i] - 1][i] = energyMatrix[seam[i]][i];
                }
            }
            for (int j = 0; j < seam[i]; j++) {
                trimmedEnergyMatrix[j][i] = energyMatrix[j][i];
            }
            for (int j = seam[i] + 1; j < energyMatrix.length; j++) {
                trimmedEnergyMatrix[j - 1][i] = energyMatrix[j][i];
            }
        }
        energyMatrix = trimmedEnergyMatrix;
    }

    private void validateSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("Seam is null");
        }
        if (seam.length != colorMatrix[0].length) {
            throw new IllegalArgumentException("Invalid seam length");
        }
        if (seam[0] < 0 || seam[0] > colorMatrix.length - 1) {
            throw new IllegalArgumentException("Invalid seam value");
        }
        for (int i = 1; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] > colorMatrix.length - 1) {
                throw new IllegalArgumentException("Invalid seam value");
            }
            if (Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException("Invalid seams difference");
            }
        }
    }

    public static void main(String[] args) {
        SeamCarver sc = new SeamCarver(new Picture(args[0]));
        int[] seam = {6, 6, 6, 6, 7, 8, 8, 8, 9, 8, 9, 9};

        sc.removeVerticalSeam(seam);
    }
}