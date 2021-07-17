import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private final Picture picture;
    private double[][] energy;

    // TODO check corner cases
    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture);
        energy = new double[picture.width()][picture.height()];
        for (int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
                energy[i][j] = energy(i, j);
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
        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) {
            return 1000;
        }
        int rgbX1 = picture.getRGB(x - 1, y);
        int rx1 = (rgbX1 >> 16) & 0xFF;
        int gx1 = (rgbX1 >> 8) & 0xFF;
        int bx1 = (rgbX1) & 0xFF;
        int rgbX2 = picture.getRGB(x + 1, y);
        int rx2 = (rgbX2 >> 16) & 0xFF;
        int gx2 = (rgbX2 >> 8) & 0xFF;
        int bx2 = (rgbX2) & 0xFF;
        int rgbY1 = picture.getRGB(x, y - 1);
        int ry1 = (rgbY1 >> 16) & 0xFF;
        int gy1 = (rgbY1 >> 8) & 0xFF;
        int by1 = (rgbY1) & 0xFF;
        int rgbY2 = picture.getRGB(x, y + 1);
        int ry2 = (rgbY2 >> 16) & 0xFF;
        int gy2 = (rgbY2 >> 8) & 0xFF;
        int by2 = (rgbY2) & 0xFF;
        double dXsqr = (rx2 - rx1) * (rx2 - rx1) + (gx2 - gx1) * (gx2 - gx1) + (bx2 - bx1) * (bx2 - bx1);
        double dYsqr = (ry2 - ry1) * (ry2 - ry1) + (gy2 - gy1) * (gy2 - gy1) + (by2 - by1) * (by2 - by1);

        return Math.sqrt(dXsqr + dYsqr);
    }

    public int[] findHorizontalSeam() {
        double[][] energyT = transposeEnergy(energy);
        return new PictureSP(energyT, 0, 0).getSeam();
    }

    private double[][] transposeEnergy(double[][] energy) {
        double[][] energyT = new double[energy[0].length][energy.length];
        for (int x = 0; x < energyT.length; x++) {
            for (int y = 0; y < energyT[0].length; y++) {
                energyT[x][y] = energy[y][x];
            }
        }
        return energyT;
    }

    public int[] findVerticalSeam() {
        return new PictureSP(energy, 3, 0).getSeam();
    }

    public void removeHorizontalSeam(int[] seam) {

    }

    public void removeVerticalSeam(int[] seam) {

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