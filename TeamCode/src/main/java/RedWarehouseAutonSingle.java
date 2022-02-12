import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
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
    public static final double INTAKE_POSITION = 0.3;
    public static final double LIFT_POSITION = 0.40;
    public static final double DROP_POSITION = 0.85;

    //liftMotor Stages
    public static final int BOTTOM_LEVEL_POSITION = 1200;
    public static final int MIDDLE_LEVEL_POSITION = 1600;
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
        carouselServo = hardwareMap.get(CRServo.class, "leftCarouselServo");
        carouselServo.setDirection(DcMotorSimple.Direction.FORWARD);
        bucketServo.setDirection(Servo.Direction.REVERSE);
        //OpenCV Recognition
        detectedLevel = bd.getZone();

        bucketServo.setPosition(INTAKE_POSITION);
        waitForStart();
        if (isStopRequested()) return;

        Pose2d startPose = new Pose2d(0, 0, 0);
        drive.setPoseEstimate(startPose);
        int level = 0;
        if(bd.getZone() == TOP_LEVEL) {
            Trajectory rightTen = drive.trajectoryBuilder(startPose)
                    .strafeRight(10)
                    .build();
            Trajectory dropBlock = drive.trajectoryBuilder(rightTen.end())
                    .lineToLinearHeading(new Pose2d(18.5, 24, Math.toRadians(14)))
                    .build();
            Trajectory goBack = drive.trajectoryBuilder(dropBlock.end())
                    .lineToLinearHeading(new Pose2d(0,24, Math.toRadians(90)))
                    .build();
            Trajectory goWareHouse = drive.trajectoryBuilder(goBack.end())
                    .lineToLinearHeading(new Pose2d(0,-36, Math.toRadians(90)),SampleMecanumDrive.getVelocityConstraint(30 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();
            Trajectory goBack2 = drive.trajectoryBuilder(goWareHouse.end())
                    .lineToLinearHeading(new Pose2d(0,24, Math.toRadians(90)))
                    .build();
            Trajectory dropBlock2 = drive.trajectoryBuilder(goBack2.end())
                    .lineToLinearHeading(new Pose2d(18.5, 24, Math.toRadians(14)))
                    .build();
            Trajectory goBack3 = drive.trajectoryBuilder(dropBlock2.end())
                    .lineToLinearHeading(new Pose2d(0,24, Math.toRadians(90)))
                    .build();
            Trajectory goWareHouse2 = drive.trajectoryBuilder(goBack3.end())
                    .lineToLinearHeading(new Pose2d(0,-36, Math.toRadians(90)),SampleMecanumDrive.getVelocityConstraint(30 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();
            Trajectory left10 = drive.trajectoryBuilder(goWareHouse2.end())
                    .strafeLeft(10)
                    .build();

            drive.followTrajectory(rightTen);
            drive.followTrajectory(dropBlock);
            dropBlock(TOP_LEVEL);
            liftReset();
            drive.followTrajectory(goBack);
            Thread.sleep(100);
            drive.followTrajectory(goWareHouse);
            intakeMotor.setPower(0.5);
            Thread.sleep(1000);
            drive.followTrajectory(goBack2);
            Thread.sleep(100);
            intakeMotor.setPower(0);
            drive.followTrajectory(dropBlock2);
            dropBlock(TOP_LEVEL);
            liftReset();
            drive.followTrajectory(goBack3);
            Thread.sleep(100);
            drive.followTrajectory(goWareHouse2);
            drive.followTrajectory(left10);
        } else if(bd.getZone() == MIDDLE_LEVEL) {
            Trajectory dropBlock = drive.trajectoryBuilder(startPose)
                    .lineToLinearHeading(new Pose2d(19, 19, Math.toRadians(28)))
                    .build();
            Trajectory goBack = drive.trajectoryBuilder(dropBlock.end())
                    .lineToLinearHeading(new Pose2d(0,24, Math.toRadians(90)))
                    .build();
            Trajectory goWareHouse = drive.trajectoryBuilder(goBack.end())
                    .lineToLinearHeading(new Pose2d(0,-30, Math.toRadians(90)),SampleMecanumDrive.getVelocityConstraint(30 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();

            drive.followTrajectory(dropBlock);
            dropBlock(MIDDLE_LEVEL);
            liftReset();
            drive.followTrajectory(goBack);

            Thread.sleep(100);
            drive.followTrajectory(goWareHouse);

        } else if(bd.getZone() == BOTTOM_LEVEL){
            Trajectory dropBlock = drive.trajectoryBuilder(startPose)
                    .lineToLinearHeading(new Pose2d(19, 19, Math.toRadians(28)))
                    .build();
            Trajectory goBack = drive.trajectoryBuilder(dropBlock.end())
                    .lineToLinearHeading(new Pose2d(0,24, Math.toRadians(90)))
                    .build();
            Trajectory goWareHouse = drive.trajectoryBuilder(goBack.end())
                    .lineToLinearHeading(new Pose2d(0,-30, Math.toRadians(90)),SampleMecanumDrive.getVelocityConstraint(30 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();

            drive.followTrajectory(dropBlock);
            dropBlock(BOTTOM_LEVEL);
            liftReset();
            drive.followTrajectory(goBack);
            Thread.sleep(100);
            drive.followTrajectory(goWareHouse);
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
    /*public void spinCarousel() throws InterruptedException {

        carouselServo.setPower(0.45);
        Thread.sleep(100);
    }
     */
}
