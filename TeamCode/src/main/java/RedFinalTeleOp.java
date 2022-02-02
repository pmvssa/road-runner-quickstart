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

@TeleOp(name="RedFinalTeleOp", group="Linear Opmode")
public class RedFinalTeleOp extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();

    //Hardware
    public DcMotorEx intakeMotor;
    public Servo bucketServo;
    public CRServo leftCarouselServo;
    public CRServo rightCarouselServo;
    public Servo capServo;

    //otherVariables
    public final double intakePosition = 0.31; //for bucketServo
    public final double liftPosition = 0.41; //while going up
    public final double dropPosition = 0.85; //to drop element
    public boolean onOff = false;
    public boolean turtleMode = false;
    public static final double NORMAL_SPEED = 0.9;
    public static final double TURTLE_SPEED = 0.35;
    public double robotSpeed = NORMAL_SPEED;

    //public DistanceSensor rdsSensorLeft;
    //public DistanceSensor rdsSensorRight;

    public DcMotorEx liftMotor;


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
            //rdsSensorLeft = hardwareMap.get(DistanceSensor.class, "rdsSensor");
            //rdsSensorRight = hardwareMap.get(DistanceSensor.class, "rdsSensor");
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
                if(intakeMotor.getCurrentPosition() > 400) { //to counteract the slip
                    intakeMotor.setPower(0.05);
                }
                else {
                    intakeMotor.setPower(0);
                }
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

            //Lift //base(2.5 rot), mid (3 rot), high (4) -  for auton
            if(gamepad2.left_trigger != 0 || gamepad1.left_trigger != 0 && liftMotor.getCurrentPosition() >= -100) {
                liftMotor.setPower(-0.45);
            }
            else if(gamepad2.right_trigger != 0 || gamepad1.right_trigger != 0 && liftMotor.getCurrentPosition() <= 2100) {
//                if(liftMotor.getCurrentPosition() >= 1200) {
//                    liftMotor.setPower(0.5);
//                }
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
            if(gamepad2.left_stick_y < 0) {
                capServo.setPosition(capServo.getPosition() - 0.04);
                Thread.sleep(150);
            }
            else if(gamepad2.left_stick_y > 0) {
                capServo.setPosition(capServo.getPosition() + 0.04);
                Thread.sleep(150);
            }
            else if(gamepad2.dpad_up) {
                capServo.setPosition(0);
            }
            else if(gamepad2.dpad_down) {
                capServo.setPosition(0.5);
            }
            else {
                capServo.setPosition(capServo.getPosition());
            }

            /*
            //distanceMode Enabled
            if (rdsSensor.getDistance(DistanceUnit.INCH) < 35) {
                double distance = rdsSensor.getDistance(DistanceUnit.INCH);
                telemetry.addData("DETECTED: ", distance);
                if(distance > 15) {
                    robotSpeed = 0.5;
                }
                else if(distance > 10 && distance < 15) {
                    robotSpeed = 0.3;
                }
                else if(distance < 10) {
                    robotSpeed = 0.1;
                }
            }
            else {
                telemetry.addData("Not Detected", "rip");
                robotSpeed = NORMAL_SPEED;
            }
            */

            //movement
            drive.setWeightedDrivePower(
                    new Pose2d(
                            -gamepad1.left_stick_y * robotSpeed,
                            -gamepad1.left_stick_x * robotSpeed,
                            -gamepad1.right_stick_x * robotSpeed
                    )
            );

            // Update everything. Odometry. Etc.
            drive.update();

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            //telemetry.addData("Motor Power ", drive.getWheelVelocities());
            telemetry.addData("LiftMotor Position: ", liftMotor.getCurrentPosition());
            telemetry.addData("BucketServo Position: ", bucketServo.getPosition());
            telemetry.addData("DriveMode: ", (turtleMode)? ("turtleMode"):("Normal"));
            telemetry.addData("Normal Speed: ", robotSpeed);
            telemetry.update();
        }
    }
}



