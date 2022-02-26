package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedLight;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.SampleMecanumDrive;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class RedWareHouseAutonMeepMeep {
    public static void main(String[] args) {
        // Declare a MeepMeep instance
        // With a field size of 800 pixels
        MeepMeep meepMeep = new MeepMeep(800);
        int xStart = 6;
        int yStart = -63;
        double headingStart = Math.toRadians(90);

        //myStatic variables
        int TOP_LEVEL = 3;
        int MIDDLE_LEVEL = 2;
        int BOTTOM_LEVEL = 1;

        //Enter Level;
        int currLevel = 3;

        Pose2d startPose = new Pose2d(xStart,yStart, headingStart);

        if(currLevel == 3) {
            RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                    // Required: Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                    .setConstraints(60, 60, 4.95, 4.95, 12.07)
                    .setDimensions(13, 16)
                    .setColorScheme(new ColorSchemeRedLight())
                    .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(startPose)
                            .lineToLinearHeading(new Pose2d(xStart - 12, yStart + 23, headingStart + Math.toRadians(27)))
                            .lineToLinearHeading(new Pose2d(xStart - 19 , yStart, headingStart + Math.toRadians(90)))
                            .lineToLinearHeading(new Pose2d(xStart + 36, yStart , headingStart + Math.toRadians(90)))
                            .strafeRight(20)
                            .build());

//            Trajectory dropBlock = drive.trajectoryBuilder(startPose)
//                    .lineToLinearHeading(new Pose2d(23, 12, Math.toRadians(27)))
//                    .build();
//            Trajectory goBack = drive.trajectoryBuilder(dropBlock.end())
//                    .lineToLinearHeading(new Pose2d(0,19, Math.toRadians(90)))
//                    .build();
//            Trajectory warehousePark = drive.trajectoryBuilder(goBack.end())
//                    .lineToLinearHeading(new Pose2d(0,-36, Math.toRadians(90)))
//                    .build();
//            Trajectory right = drive.trajectoryBuilder(warehousePark.end())
//                    .strafeRight(20)
//                    .build();
            // Set field image
            meepMeep.setBackground(MeepMeep.Background.FIELD_FREIGHTFRENZY_ADI_DARK)
                    .setDarkMode(true)
                    // Background opacity from 0-1
                    .setBackgroundAlpha(0.95f)
                    .addEntity(myBot)
                    .start();
        }
        if(currLevel == 2) {
            RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                    // Required: Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                    .setConstraints(60, 60, 4.95, 4.95, 12.07)
                    .setDimensions(13, 16)
                    .setColorScheme(new ColorSchemeRedLight())
                    .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(startPose)
                            .lineToLinearHeading(new Pose2d(xStart + 15, yStart + 25.5, headingStart + Math.toRadians(-38)))
                            .waitSeconds(1.5)
                            .lineToLinearHeading(new Pose2d(xStart, yStart + 5.5,headingStart + Math.toRadians(-36)))
                            .lineToLinearHeading(new Pose2d(xStart - 18, yStart + 5.5, headingStart))
                            .lineToLinearHeading(new Pose2d(xStart -20, yStart + 5.5, headingStart), SampleMecanumDrive.getVelocityConstraint(12, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(90))
                            .waitSeconds(1.5)
                            .lineToLinearHeading(new Pose2d(xStart - 16, yStart, headingStart + Math.toRadians(-15)), SampleMecanumDrive.getVelocityConstraint(5, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(90))
                            .lineToLinearHeading(new Pose2d(xStart - 10, yStart + 0.5, headingStart), SampleMecanumDrive.getVelocityConstraint(10, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(90))
                            .lineToLinearHeading(new Pose2d(xStart + 15, yStart + 25.5, headingStart + Math.toRadians(-39)))
                            .waitSeconds(1.5)
                            .lineToLinearHeading(new Pose2d(xStart + 27, yStart, headingStart + Math.toRadians(90)))
                            .back(50)
                            .build());

//                    .lineToLinearHeading(new Pose2d(25.5, -15, Math.toRadians(-38)))
//                    .lineToLinearHeading(new Pose2d(5.5, 0, Math.toRadians(-36)))
//                    .lineToLinearHeading(new Pose2d(5.5, 18))
//                    .lineToLinearHeading(new Pose2d(5.5, 20), SampleMecanumDrive.getVelocityConstraint(12, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(90))
//                    .lineToLinearHeading(new Pose2d(0, 16, Math.toRadians(-15)), SampleMecanumDrive.getVelocityConstraint(5, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(90))
//                    .lineToLinearHeading(new Pose2d(0.5, 10), SampleMecanumDrive.getVelocityConstraint(10, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(90))
//                    .lineToLinearHeading(new Pose2d(25.5, -15, Math.toRadians(-39)))
//                    .lineToLinearHeading(new Pose2d(0, -27, Math.toRadians(90)))
//                    .back(50)
//                    .build());
            // Set field image
            meepMeep.setBackground(MeepMeep.Background.FIELD_FREIGHTFRENZY_ADI_DARK)
                    .setDarkMode(true)
                    // Background opacity from 0-1
                    .setBackgroundAlpha(0.95f)
                    .addEntity(myBot)
                    .start();
        }
        if(currLevel == 1) {
            RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                    // Required: Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                    .setConstraints(60, 60, 4.95, 4.95, 12.07)
                    .setDimensions(13, 16)
                    .setColorScheme(new ColorSchemeRedLight())
                    .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(startPose)
                            .lineToLinearHeading(new Pose2d(xStart + 14.25, yStart + 24.5, headingStart + Math.toRadians(-40)))
                            .waitSeconds(1.5)
                            .lineToLinearHeading(new Pose2d(xStart, yStart + 5.5,headingStart + Math.toRadians(-36)))
                            .lineToLinearHeading(new Pose2d(xStart - 18, yStart + 5.5, headingStart))
                            .lineToLinearHeading(new Pose2d(xStart -20, yStart + 5.5, headingStart), SampleMecanumDrive.getVelocityConstraint(12, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(90))
                            .waitSeconds(1.5)
                            .lineToLinearHeading(new Pose2d(xStart - 16, yStart, headingStart + Math.toRadians(-05)), SampleMecanumDrive.getVelocityConstraint(5, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(90))
                            .lineToLinearHeading(new Pose2d(xStart - 0, yStart + 0.5, headingStart), SampleMecanumDrive.getVelocityConstraint(10, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(90))
                            .lineToLinearHeading(new Pose2d(xStart + 15, yStart + 26, headingStart + Math.toRadians(-39)))
                            .waitSeconds(1.5)
                            .lineToLinearHeading(new Pose2d(xStart + 27, yStart, headingStart + Math.toRadians(90)))
                            .back(50)
                            .build());

//                    .lineToLinearHeading(new Pose2d(24.50, -14.25, Math.toRadians(-40)))
//                    .lineToLinearHeading(new Pose2d(5.5, 0, Math.toRadians(-36)))
//                    .lineToLinearHeading(new Pose2d(5.5, 18))

//                    .lineToLinearHeading(new Pose2d(5.5, 20), SampleMecanumDrive.getVelocityConstraint(12, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
//                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
//
//                    .lineToLinearHeading(new Pose2d(0, 16, Math.toRadians(-10)), SampleMecanumDrive.getVelocityConstraint(5, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
//                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL)).build();
//                    .lineToLinearHeading(new Pose2d(0.5, 0), SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
//                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL)).build();
//                    .lineToLinearHeading(new Pose2d(26, -15, Math.toRadians(-38)))
//                    .build();
//                    .lineToLinearHeading(new Pose2d(0, -27, Math.toRadians(90)))
//                    .build();
//                    .back(50)
//                    .build();
            // Set field image
            meepMeep.setBackground(MeepMeep.Background.FIELD_FREIGHTFRENZY_ADI_DARK)
                    .setDarkMode(true)
                    // Background opacity from 0-1
                    .setBackgroundAlpha(0.95f)
                    .addEntity(myBot)
                    .start();
        }

    }
}
