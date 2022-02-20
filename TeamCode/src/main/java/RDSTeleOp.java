import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

@TeleOp(name="RDS TeleOp", group="RDSTeleOp")
@Disabled
public class RDSTeleOp extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();

    //Hardware
    public boolean turtleMode = false;
    public Servo bucketServo;
    public static final double NORMAL_SPEED = 0.85;
    public static final double TURTLE_SPEED = 0.35;
    public double robotSpeed = NORMAL_SPEED;

    public DistanceSensor rdsSensor;

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

        rdsSensor = hardwareMap.get(DistanceSensor.class, "rdsSensor");
        bucketServo = hardwareMap.get(Servo.class, "bucketServo");


        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();
        bucketServo.setPosition(0.41);


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {


            //turtleMode
            if(gamepad1.y && !turtleMode) {
                turtleMode = true;
                robotSpeed = TURTLE_SPEED;
            }
            else if (gamepad1.y && turtleMode) {
                turtleMode = false;
                robotSpeed = NORMAL_SPEED;
            }

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
            telemetry.addData("DriveMode: ", (turtleMode)? ("turtleMode"):("Normal"));
            telemetry.addData("Normal Speed: ", robotSpeed);
            telemetry.update();
        }
    }
}

/*
package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

public class RDSTeleOp extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();

    //Hardware
    public boolean turtleMode = false;
    public static final double NORMAL_SPEED = 0.85;
    public static final double TURTLE_SPEED = 0.35;
    public double robotSpeed = NORMAL_SPEED;

    public DistanceSensor rdsSensor;

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

        rdsSensor = hardwareMap.get(DistanceSensor.class, "rdsSensor");

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {


            //turtleMode
            if(gamepad1.y && !turtleMode) {
                turtleMode = true;
                robotSpeed = TURTLE_SPEED;
            }
            else if (gamepad1.y && turtleMode) {
                turtleMode = false;
                robotSpeed = NORMAL_SPEED;
            }

            //distanceMode Enabled
            if (rdsSensor.getDistance(DistanceUnit.INCH) < 35) {
                double distance = rdsSensor.getDistance(DistanceUnit.INCH);
                robotSpeed = ((distance-7)/35) * NORMAL_SPEED;
            }


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
            telemetry.addData("Motor Power ", drive.getWheelVelocities());
            telemetry.addData("DriveMode: ", (turtleMode)? ("turtleMode"):("Normal"));
            telemetry.update();
        }
    }
}

 */
