import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private final Picture picture;
    private final double[][] energyMatrix;
    private final int[][] colorMatrix;
    private int width;
    private int height;

    // TODO check corner cases
    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture);
        width = picture.width();
        height = picture.height();
        energyMatrix = new double[picture.width()][picture.height()];
        for (int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
                energyMatrix[i][j] = energy(i, j);
            }
        }
        colorMatrix = new int[picture.width()][picture.height()];
        for (int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
                colorMatrix[i][j] = this.picture.getRGB(i, j);
            }
        }

    }

    public Picture picture() {
        return new Picture(picture);
    }

    public int width() {
        return picture.width();
    }

    public int height() {
        return picture.height();
    }

    public double energy(int x, int y) {
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
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

        height--;
    }

    public void removeVerticalSeam(int[] seam) {
        for (int i = 0; i < colorMatrix[0].length; i++) {
            for (int j = seam[i]; j < colorMatrix.length - 1; j++) {
                colorMatrix[j][i] = colorMatrix[j + 1][i];
            }
            colorMatrix[colorMatrix.length - 1][i] = -1;
        }
        width--;
        for (int i = 0; i < energyMatrix[0].length; i++) {
            if (seam[i] > 0) {
                energyMatrix[seam[i] - 1][i] = energy(seam[i] - 1, i);
            }
            energyMatrix[seam[i] + 1][i] = energy(seam[i], i);
            for (int j = seam[i]; j < energyMatrix.length - 1; j++) {
                energyMatrix[j][i] = energyMatrix[j + 1][i];
            }
            energyMatrix[energyMatrix.length - 1][i] = -1;
        }
    }

    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
//        System.out.printf("image is %d columns by %d rows\n", picture.width(), picture.height());
//        picture.show();
        SeamCarver sc = new SeamCarver(picture);

        for (int i : sc.findVerticalSeam()) {
            System.out.print(i + " ");
        }
    }
}