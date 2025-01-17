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
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.lang.reflect.Member;

@Autonomous(name = "BlueCarouselAuton_ShippingUnitPark", group = "Autonomous")
public class BlueCarouselAuton_ShippingUnitPark extends LinearOpMode {

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
    public static final double LIFT_POSITION = 0.41;
    public static final double DROP_POSITION = 0.85;

    //liftMotor Stages
    public static final int BOTTOM_LEVEL_POSITION = 1150;
    public static final int MIDDLE_LEVEL_POSITION = 1485;
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
        carouselServo = hardwareMap.get(CRServo.class, "rightCarouselServo");
        carouselServo.setDirection(DcMotorSimple.Direction.FORWARD);
        bucketServo.setDirection(Servo.Direction.REVERSE);
        //OpenCV Recognition
        detectedLevel = bd.getZone();

        bucketServo.setPosition(INTAKE_POSITION);
        waitForStart();
        if (isStopRequested()) return;
        //frontCamCV.stopStreaming();

        Pose2d startPose = new Pose2d(0, 0, 0);
        drive.setPoseEstimate(startPose);

        bucketServo.setPosition(LIFT_POSITION);
        if(bd.getZone() == TOP_LEVEL){
            Trajectory dropBlock = drive.trajectoryBuilder(startPose)
                    .lineToLinearHeading(new Pose2d(25, 9.5, Math.toRadians(35)))
                    .build();
            Trajectory goBack = drive.trajectoryBuilder(dropBlock.end())
                    .lineToLinearHeading(new Pose2d(4,0))
                    .build();
            Trajectory partCarousel = drive.trajectoryBuilder(goBack.end())
                    .lineToLinearHeading(new Pose2d(6, -20))
                    .build();
            Trajectory carousel = drive.trajectoryBuilder(partCarousel.end())
                    .lineToLinearHeading(new Pose2d(6,-26.5),SampleMecanumDrive.getVelocityConstraint(10 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();
            Trajectory pickDuck = drive.trajectoryBuilder(carousel.end())
                    .lineToLinearHeading(new Pose2d(0.5,-22, Math.toRadians(15)),SampleMecanumDrive.getVelocityConstraint(5 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();
            Trajectory moreIntake = drive.trajectoryBuilder(pickDuck.end())
                    .lineToLinearHeading(new Pose2d(0.5,-15),SampleMecanumDrive.getVelocityConstraint(10 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();
            Trajectory dropBlock2 = drive.trajectoryBuilder(moreIntake.end())
                    .lineToLinearHeading(new Pose2d(24.25, 8.25, Math.toRadians(36)))
                    .build();
            Trajectory backten = drive.trajectoryBuilder(dropBlock2.end())
                    .lineToLinearHeading(new Pose2d(5,-20))
                    .build();
            Trajectory park = drive.trajectoryBuilder(backten.end())
                    .lineToLinearHeading(new Pose2d(29, -30))
                    .build();
            //Thread.sleep(3000);
            drive.followTrajectory(dropBlock);
            dropBlock(TOP_LEVEL);
            liftReset();
            drive.followTrajectory(goBack);
            drive.followTrajectory(partCarousel);
            drive.followTrajectory(carousel);
            spinCarousel();
            intakeMotor.setPower(0.7);
            Thread.sleep(6000);
            drive.followTrajectory(pickDuck);
            drive.followTrajectory(moreIntake);
            intakeMotor.setPower(0);
            drive.followTrajectory(dropBlock2);
            dropBlock(TOP_LEVEL);
            liftReset();
            drive.followTrajectory(backten);

            drive.followTrajectory(park);

        }  else if(bd.getZone() == MIDDLE_LEVEL){
            Trajectory dropBlock = drive.trajectoryBuilder(startPose)
                    .lineToLinearHeading(new Pose2d(25.8, 8.25, Math.toRadians(40)))
                    .build();
            Trajectory goBack = drive.trajectoryBuilder(dropBlock.end())
                    .lineToLinearHeading(new Pose2d(4,0))
                    .build();
            Trajectory partCarousel = drive.trajectoryBuilder(goBack.end())
                    .lineToLinearHeading(new Pose2d(6, -20))
                    .build();
            Trajectory carousel = drive.trajectoryBuilder(partCarousel.end())
                    .lineToLinearHeading(new Pose2d(6,-26),SampleMecanumDrive.getVelocityConstraint(8 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();
            Trajectory pickDuck = drive.trajectoryBuilder(carousel.end())
                    .lineToLinearHeading(new Pose2d(0.5,-22, Math.toRadians(15)),SampleMecanumDrive.getVelocityConstraint(5 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();
            Trajectory moreIntake = drive.trajectoryBuilder(pickDuck.end())
                    .lineToLinearHeading(new Pose2d(0.5,-10),SampleMecanumDrive.getVelocityConstraint(10 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();
            Trajectory dropBlock2 = drive.trajectoryBuilder(moreIntake.end())
                    .lineToLinearHeading(new Pose2d(25, 8.25, Math.toRadians(40)))
                    .build();
            Trajectory backten = drive.trajectoryBuilder(dropBlock2.end())
                    .lineToLinearHeading(new Pose2d(3,-10))
                    .build();
            Trajectory park = drive.trajectoryBuilder(backten.end())
                    .lineToLinearHeading(new Pose2d(29, -30))
                    .build();
            drive.followTrajectory(dropBlock);
            dropBlock(MIDDLE_LEVEL);
            liftReset();
            drive.followTrajectory(goBack);
            drive.followTrajectory(partCarousel);
            drive.followTrajectory(carousel);
            spinCarousel();
            intakeMotor.setPower(0.7);
            Thread.sleep(5000);
            carouselServo.setPower(0);
            drive.followTrajectory(pickDuck);
            drive.followTrajectory(moreIntake);
            bucketServo.setPosition(LIFT_POSITION);
            intakeMotor.setPower(0);
            drive.followTrajectory(dropBlock2);
            dropBlock(TOP_LEVEL);
            liftReset();
            drive.followTrajectory(backten);
            drive.followTrajectory(park);
        } else if(bd.getZone() == BOTTOM_LEVEL){
            Trajectory dropBlockA = drive.trajectoryBuilder(startPose)
                    .lineToLinearHeading(new Pose2d(40, -9, Math.toRadians(90))).build();

            Trajectory dropBlockB = drive.trajectoryBuilder(dropBlockA.end())
                    .lineToLinearHeading(new Pose2d(40, 0.25, Math.toRadians(90))).build();

            Trajectory goBackA = drive.trajectoryBuilder(dropBlockB.end())
                    .lineToLinearHeading(new Pose2d(40, -10, Math.toRadians(0))).build();

            Trajectory partCarousel = drive.trajectoryBuilder(goBackA.end())
                    .lineToLinearHeading(new Pose2d(6, -20))
                    .build();
            Trajectory carousel = drive.trajectoryBuilder(partCarousel.end())
                    .lineToLinearHeading(new Pose2d(6,-26),SampleMecanumDrive.getVelocityConstraint(8 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();
            Trajectory pickDuck = drive.trajectoryBuilder(carousel.end())
                    .lineToLinearHeading(new Pose2d(0.5,-22, Math.toRadians(11)),SampleMecanumDrive.getVelocityConstraint(5 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();
            Trajectory moreIntake = drive.trajectoryBuilder(pickDuck.end())
                    .lineToLinearHeading(new Pose2d(0.5,-12),SampleMecanumDrive.getVelocityConstraint(10 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                    .build();
            Trajectory dropBlockC = drive.trajectoryBuilder(moreIntake.end())
                    .lineToLinearHeading(new Pose2d(40, -9, Math.toRadians(90))).build();

            Trajectory dropBlockD = drive.trajectoryBuilder(dropBlockC.end())
                    .lineToLinearHeading(new Pose2d(40, -0.5, Math.toRadians(90))).build();

            Trajectory park = drive.trajectoryBuilder(dropBlockD.end())
                    .lineToLinearHeading(new Pose2d(28, -28)).build();

            //dropping first block
            drive.followTrajectory(dropBlockA);
            drive.followTrajectory(dropBlockB);
            dropBlock(BOTTOM_LEVEL);
            liftReset();
            drive.followTrajectory(goBackA);

            //delivering and collecting the duck
            drive.followTrajectory(partCarousel);
            drive.followTrajectory(carousel);
            spinCarousel();
            intakeMotor.setPower(0.7);
            Thread.sleep(4000);
            carouselServo.setPower(0);
            drive.followTrajectory(pickDuck);
            drive.followTrajectory(moreIntake);
            bucketServo.setPosition(LIFT_POSITION);
            intakeMotor.setPower(0);

            //dropping duck
            drive.followTrajectory(dropBlockC);
            drive.followTrajectory(dropBlockD);
            dropBlock(TOP_LEVEL);
            liftReset();

            //parking
            drive.followTrajectory(park);
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
            Thread.sleep(1500);
        }
        bucketServo.setPosition(DROP_POSITION);
        Thread.sleep(750);
    }

    public void liftReset() throws InterruptedException {
        bucketServo.setPosition(LIFT_POSITION);
        Thread.sleep(600);
        liftMotor.setTargetPosition(0);
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotor.setPower(-0.60);
        Thread.sleep(2000);
        bucketServo.setPosition(INTAKE_POSITION);
    }
    public void spinCarousel() throws InterruptedException {
        carouselServo.setPower(0.7);
    }
}
