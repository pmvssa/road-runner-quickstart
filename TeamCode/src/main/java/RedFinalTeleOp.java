import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import java.util.concurrent.TimeUnit;

@TeleOp(name="RedFinalTeleOp", group="Linear Opmode")
public class RedFinalTeleOp extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();

    //Hardware
    public DcMotorEx liftMotor;
    public DcMotorEx intakeMotor;
    public Servo bucketServo;
    public CRServo leftCarouselServo;
    public CRServo rightCarouselServo;
    public Servo capServo;

    //otherVariables
    public final double intakePosition = 0.35; //for bucketServo
    public final double liftPosition = 0.45; //while going up
    public final double dropPosition = 0.87; //to drop element
    public boolean onOff = false;
    public boolean turtleMode = false;
    public boolean rds = true;
    public static final double NORMAL_SPEED = 0.8;
    public static final double TURTLE_SPEED = 0.25;
    public double robotSpeed = NORMAL_SPEED;

    public DistanceSensor rdsSensorLeft;
    public DistanceSensor rdsSensorRight;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        drive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        rdsSensorLeft = hardwareMap.get(DistanceSensor.class, "rdsSensorLeft");
        rdsSensorRight = hardwareMap.get(DistanceSensor.class, "rdsSensorRight");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        bucketServo = hardwareMap.get(Servo.class, "bucketServo");
        capServo = hardwareMap.get(Servo.class, "capServo");
        leftCarouselServo = hardwareMap.get(CRServo.class, "leftCarouselServo");
        rightCarouselServo = hardwareMap.get(CRServo.class, "rightCarouselServo");
        leftCarouselServo.setDirection(DcMotorSimple.Direction.FORWARD);
        rightCarouselServo.setDirection(DcMotorSimple.Direction.REVERSE);

        liftMotor = hardwareMap.get(DcMotorEx.class, "liftMotor");

        liftMotor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        bucketServo.setDirection(Servo.Direction.REVERSE);
        bucketServo.setPosition(intakePosition);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            //intakeMotor
            if(gamepad2.right_bumper || gamepad1.right_bumper) {
                intakeMotor.setPower(-0.7); //out-take
            }
            else if(gamepad2.left_bumper || gamepad1.left_bumper) {
                intakeMotor.setPower(0.9); //intake
            }
            else {
                intakeMotor.setPower(0);
            }

            //bucketServo drop
            if(gamepad2.dpad_left || gamepad1.dpad_left) {
                bucketServo.setDirection(Servo.Direction.REVERSE);
                bucketServo.setPosition(dropPosition);
            }
            else if(gamepad2.dpad_right || gamepad1.dpad_right) {
                bucketServo.setDirection(Servo.Direction.REVERSE);
                bucketServo.setPosition(liftPosition);
            }
            else if(gamepad2.a || gamepad1.a) {
                bucketServo.setDirection(Servo.Direction.REVERSE);
                bucketServo.setPosition(intakePosition);
            }

            //carousel
            if(gamepad2.y && !onOff) {
                onOff = true;
                rightCarouselServo.setPower(0.69);
            }
            else if (gamepad2.x && !onOff) {
                onOff = true;
                rightCarouselServo.setPower(0);
            } else{
                onOff = false;
            }

            //Lift
            if(gamepad2.left_trigger != 0 || gamepad1.left_trigger != 0 && liftMotor.getCurrentPosition() >= -100) {
                liftMotor.setPower(-0.45);
            }
            else if(gamepad2.right_trigger != 0 || gamepad1.right_trigger != 0 && liftMotor.getCurrentPosition() <= 2100) {
                if(liftMotor.getCurrentPosition() >= 1200) {
                    liftMotor.setPower(0.5);
               }
                liftMotor.setPower(0.85);
            }
            else {
                liftMotor.setPower(0.0);
                if(liftMotor.getCurrentPosition() >= 1000) {
                    liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                }
                else {
                    liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                }
            }

            //lift + bucket reset
            if((gamepad2.b || gamepad1.b) && liftMotor.getCurrentPosition() > 0) {
                bucketServo.setPosition(liftPosition);
                liftMotor.setPower(-0.30);
            }

            //turtleMode
            if(gamepad1.y && !turtleMode) {
                turtleMode = true;
                robotSpeed = TURTLE_SPEED;
            }
            else if (gamepad1.y && turtleMode) {
                turtleMode = false;
                robotSpeed = NORMAL_SPEED;
            }

            //Capping Servo
            //stick movement
            if(gamepad2.left_stick_y < 0) {
                capServo.setPosition(capServo.getPosition() - 0.035);
                Thread.sleep(50);
            }
            else if(gamepad2.left_stick_y > 0) {
                capServo.setPosition(capServo.getPosition() + 0.035);
                Thread.sleep(50);
            }
            //dpad auto movement
            if(gamepad2.dpad_down) {
                if(capServo.getPosition() > 0.8) {
                    capServo.setDirection(Servo.Direction.FORWARD);
                    capServo.setPosition(0.85);
                }
                else {
                    capServo.setDirection(Servo.Direction.REVERSE);
                    capServo.setPosition(0.85);
                }
            }


            //RDS
            if(gamepad1.x) {
                rds = false;
            }
            if(!turtleMode && rds) {
                if(runtime.time(TimeUnit.SECONDS) < 90 && !turtleMode) {
                    if ((rdsSensorLeft.getDistance(DistanceUnit.INCH) < 35) || (rdsSensorRight.getDistance(DistanceUnit.INCH) < 35)) {
                        double distanceLeft = rdsSensorLeft.getDistance(DistanceUnit.INCH);
                        double distanceRight = rdsSensorRight.getDistance(DistanceUnit.INCH);
                        double distance = Math.min(distanceRight, distanceLeft);
                        telemetry.addData("DETECTED: ", distance);
                        if (distance < 30) {
                            robotSpeed = 0.4;
                        }
                    } else {
                        telemetry.addData("Not Detected", "rip");
                        if(!turtleMode) {
                            robotSpeed = NORMAL_SPEED;
                        }
                    }
                }
            }


            //movement
            drive.setWeightedDrivePower(
                    new Pose2d(
                            -gamepad1.left_stick_y * robotSpeed,
                            -gamepad1.left_stick_x * robotSpeed,
                            -gamepad1.right_stick_x * robotSpeed * 0.75
                    )
            );

            // Update everything. Odometry. Etc.
            drive.update();

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("DRIVE", "------------------------------------");
            telemetry.addData("Motor Power ", drive.getWheelVelocities());
            telemetry.addData("DriveMode: ", (turtleMode)? ("turtleMode"):((rds)? ("RDS"):("Normal")));
            telemetry.addData("Normal Speed: ", robotSpeed);
            telemetry.addData("OTHER", "------------------------------------");
            telemetry.addData("LiftMotor Position: ", liftMotor.getCurrentPosition());
            telemetry.addData("BucketServo Position: ", bucketServo.getPosition());
            telemetry.addData("CapServo Position: ", capServo.getPosition());
            telemetry.update();
        }
    }
}





