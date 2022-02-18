import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.util.PoseStorage;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Autonomous(name = "RedWarehouseAutonSingle", group = "Autonomous")
public class RedWarehouseAutonSingle extends LinearOpMode {

    // Declare OpMode members.
    private final ElapsedTime runtime = new ElapsedTime();

    //Hardware
    public DcMotorEx liftMotor;
    public DcMotorEx intakeMotor;
    public Servo bucketServo;
    public CRServo carouselServo;
    private OpenCvCamera frontCamCV;

    //OpenCV
    private BarcodeDetector bd;

    //bucketServo Stages
    public static final double INTAKE_POSITION = 0.35;
    public static final double LIFT_POSITION = 0.40;
    public static final double DROP_POSITION = 0.85;

    //liftMotor Stages
    public static final int BOTTOM_LEVEL_POSITION = 1200;
    public static final int MIDDLE_LEVEL_POSITION = 1550;
    public static final int TOP_LEVEL_POSITION = 2000;

    //Levels
    public static final int TOP_LEVEL = 3;
    public static final int MIDDLE_LEVEL = 2;
    public static final int BOTTOM_LEVEL = 1;
    public int detectedLevel;



    @Override
    public void runOpMode() throws InterruptedException {

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        WebcamName frontCam = hardwareMap.get(WebcamName.class, "frontCamera");
        frontCamCV = OpenCvCameraFactory.getInstance().createWebcam(frontCam, cameraMonitorViewId);
        bd = new BarcodeDetector();

        frontCamCV.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                frontCamCV.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
                frontCamCV.setPipeline(bd);

            }
            @Override
            public void onError(int errorCode)
            {
                /*
                 * This will be called if the camera could not be opened
                 */
            }
        });

        //hardware Mapping
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        bucketServo = hardwareMap.get(Servo.class, "bucketServo");
        liftMotor = hardwareMap.get(DcMotorEx.class, "liftMotor");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        //carouselServo = hardwareMap.get(CRServo.class, "leftCarouselServo");
        //carouselServo.setDirection(DcMotorSimple.Direction.FORWARD);
        bucketServo.setDirection(Servo.Direction.REVERSE);

        //OpenCV Recognition
        detectedLevel = bd.getZone();

        bucketServo.setPosition(INTAKE_POSITION);
        waitForStart();
        if (isStopRequested()) return;

        Pose2d startPose = new Pose2d(0, 0, 0);
        drive.setPoseEstimate(startPose);
        int level = 0;

        bucketServo.setPosition(LIFT_POSITION);
        if(bd.getZone() == TOP_LEVEL) {
            Trajectory dropBlock = drive.trajectoryBuilder(startPose)
                    .lineToLinearHeading(new Pose2d(23, 12, Math.toRadians(27)))
                    .build();
            Trajectory goBack = drive.trajectoryBuilder(dropBlock.end())
                    .lineToLinearHeading(new Pose2d(0,19, Math.toRadians(90)))
                    .build();
            Trajectory warehousePark = drive.trajectoryBuilder(goBack.end())
                    .lineToLinearHeading(new Pose2d(0,-36, Math.toRadians(90)))
                    .build();
            Trajectory right = drive.trajectoryBuilder(warehousePark.end())
                    .strafeRight(20)
                    .build();

            drive.followTrajectory(dropBlock);
            dropBlock(TOP_LEVEL);
            liftReset();
            drive.followTrajectory(goBack);
            Thread.sleep(100);
            drive.followTrajectory(warehousePark);
            drive.followTrajectory(right);
        } else if(bd.getZone() == MIDDLE_LEVEL) {
            Trajectory dropBlock = drive.trajectoryBuilder(startPose)
                    .lineToLinearHeading(new Pose2d(23, 12, Math.toRadians(27)))
                    .build();
            Trajectory goBack = drive.trajectoryBuilder(dropBlock.end())
                    .lineToLinearHeading(new Pose2d(0,19, Math.toRadians(90)))
                    .build();
            Trajectory warehousePark = drive.trajectoryBuilder(goBack.end())
                    .lineToLinearHeading(new Pose2d(0,-36, Math.toRadians(90)))
                    .build();
            Trajectory right = drive.trajectoryBuilder(warehousePark.end())
                    .strafeRight(20)
                    .build();

            drive.followTrajectory(dropBlock);
            dropBlock(MIDDLE_LEVEL);
            liftReset();
            drive.followTrajectory(goBack);
            Thread.sleep(100);
            drive.followTrajectory(warehousePark);
            drive.followTrajectory(right);

        } else if(bd.getZone() == BOTTOM_LEVEL){
            Trajectory goLeft = drive.trajectoryBuilder(startPose)
                    .strafeLeft(5)
                    .build();
            Trajectory dropBlock = drive.trajectoryBuilder(goLeft.end())
                    .lineToLinearHeading(new Pose2d(20, 19, Math.toRadians(10)))
                    .build();
            Trajectory goBack = drive.trajectoryBuilder(dropBlock.end())
                    .lineToLinearHeading(new Pose2d(0,19, Math.toRadians(90)))
                    .build();
            Trajectory warehousePark = drive.trajectoryBuilder(goBack.end())
                    .lineToLinearHeading(new Pose2d(0,-36, Math.toRadians(90)))
                    .build();
            Trajectory right = drive.trajectoryBuilder(warehousePark.end())
                    .strafeRight(20)
                    .build();

            drive.followTrajectory(goLeft);
            drive.followTrajectory(dropBlock);
            dropBlock(BOTTOM_LEVEL);
            liftReset();
            drive.followTrajectory(goBack);
            Thread.sleep(100);
            drive.followTrajectory(warehousePark);
            drive.followTrajectory(right);
        }

        while (!isStopRequested() && opModeIsActive()) {
            PoseStorage.currentPose = drive.getPoseEstimate();
        }

    }


    public void dropBlock (int level) throws InterruptedException {
        Thread.sleep(100);
        bucketServo.setPosition(LIFT_POSITION);
        if(level == BOTTOM_LEVEL) {
            liftMotor.setTargetPosition(BOTTOM_LEVEL_POSITION);
            liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            liftMotor.setPower(0.9);
            Thread.sleep(750);
        }
        else if(level == MIDDLE_LEVEL) {
            liftMotor.setTargetPosition(MIDDLE_LEVEL_POSITION);
            liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            liftMotor.setPower(0.9);
            Thread.sleep(850);
        }
        else if(level == TOP_LEVEL) {
            liftMotor.setTargetPosition(TOP_LEVEL_POSITION);
            liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            liftMotor.setPower(0.9);
            Thread.sleep(1000);
        }
        bucketServo.setPosition(DROP_POSITION);
        Thread.sleep(750);
    }

    public void liftReset() throws InterruptedException {
        bucketServo.setPosition(LIFT_POSITION);
        Thread.sleep(500);
        liftMotor.setTargetPosition(0);
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotor.setPower(-0.60);
        Thread.sleep(1500);
        bucketServo.setPosition(INTAKE_POSITION);
    }
    /*
    public void spinCarousel() throws InterruptedException {
        carouselServo.setPower(0.45);
        Thread.sleep(100);
    }
    */
}
