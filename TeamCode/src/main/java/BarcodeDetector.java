import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class BarcodeDetector extends OpenCvPipeline {
    private Mat workingMatrix = new Mat();

    //Levels
    public static final int TOP_LEVEL = 3;
    public static final int MIDDLE_LEVEL = 2;
    public static final int BOTTOM_LEVEL = 1;
    public static int scannedLevel;

    public BarcodeDetector() {

    }

    @Override
    public final Mat processFrame(Mat input) {
        input.copyTo(workingMatrix);

        if(workingMatrix.empty()) {
            return input;
        }

        Imgproc.cvtColor(workingMatrix, workingMatrix, Imgproc.COLOR_RGB2YCrCb);

        //Detection Areas
        Mat leftSubmat = workingMatrix.submat(200, 300, 25, 125);
        Mat middleSubmat = workingMatrix.submat(200,300,350,450);

        //Displaying Detection Areas as Rectangles
        Imgproc.rectangle(workingMatrix, new Rect(25, 200, 100, 100), new Scalar(255, 0, 0));
        Imgproc.rectangle(workingMatrix, new Rect(350,200,100,100), new Scalar(255, 0, 0));

        /*

        Resolution: width(640), height(480)
                //Detection Areas
        Mat leftSubmat = workingMatrix.submat(475, 575, 25, 125);
        Mat middleSubmat = workingMatrix.submat(475,575,400,500);

        //Displaying Detection Areas as Rectangles
        Imgproc.rectangle(workingMatrix, new Rect(25, 475, 100, 100), new Scalar(255, 0, 0));
        Imgproc.rectangle(workingMatrix, new Rect(400,475,100,100), new Scalar(255, 0, 0));
         */

        //Total of CB values
        double leftTotal = Core.sumElems(leftSubmat).val[2];
        double middleTotal = Core.sumElems(middleSubmat).val[2];


        if(Math.abs(leftTotal - middleTotal) < 50000) {
            scannedLevel = TOP_LEVEL;
            Imgproc.putText(workingMatrix, "TOP LEVEL: "  + "leftTotal: " + leftTotal + " middleTotal: " + middleTotal + "diff: " + (Math.abs(leftTotal - middleTotal)), new Point(5, 450), 0, 0.6, new Scalar(255, 0, 0));

        }
        else if(leftTotal > middleTotal) {
            scannedLevel = BOTTOM_LEVEL;
            Imgproc.putText(workingMatrix, "BOTTOM LEVEL: "  + "leftTotal: " + leftTotal + " middleTotal: " + middleTotal + "diff: " + (Math.abs(leftTotal - middleTotal)), new Point(5, 450), 0, 0.6, new Scalar(255, 0, 0));

        }
        else {
            scannedLevel = MIDDLE_LEVEL;
            Imgproc.putText(workingMatrix, "MIDDLE LEVEL: "  + "leftTotal: " + leftTotal + " middleTotal: " + middleTotal + "diff: " + (Math.abs(leftTotal - middleTotal)), new Point(5, 450), 0, 0.6, new Scalar(255, 0, 0));

        }

        return workingMatrix;
    }

    public int getZone() {
        return scannedLevel;
    }

}