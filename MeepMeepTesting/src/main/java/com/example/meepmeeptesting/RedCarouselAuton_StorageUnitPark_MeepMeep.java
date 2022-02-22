package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedLight;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.SampleMecanumDrive;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import java.util.Scanner;

public class RedCarouselAuton_WarehousePark_MeepMeep {
    public static void main(String[] args) {
        // Declare a MeepMeep instance
        // With a field size of 800 pixels
        MeepMeep meepMeep = new MeepMeep(800);
        int xStart = -36;
        int yStart = -63;
        double headingStart = Math.toRadians(90);

        //myStatic variables
        int TOP_LEVEL = 3;
        int MIDDLE_LEVEL = 2;
        int BOTTOM_LEVEL = 1;

        //Enter Level;
        int currLevel = 2;

        Pose2d startPose = new Pose2d(xStart,yStart, headingStart);

        if(currLevel == 3) {
            RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                    // Required: Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                    .setConstraints(60, 60, 4.95, 4.95, 12.07)
                    .setDimensions(13, 16)
                    .setColorScheme(new ColorSchemeRedLight())
                    .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(startPose)
                            .lineToLinearHeading(new Pose2d(xStart - 6, yStart + 36, headingStart + Math.toRadians(0)))
                            .lineToLinearHeading(new Pose2d(xStart + 7.5, yStart + 36, headingStart + Math.toRadians(-90)))
                            .waitSeconds(1.5)
                            .lineToLinearHeading(new Pose2d(xStart - 2, yStart + 36, headingStart + Math.toRadians(0)))
                            .lineToLinearHeading(new Pose2d(xStart - 19, yStart + 4.5, headingStart))
                            .lineToLinearHeading(new Pose2d(xStart - 21, yStart + 4.5, headingStart), SampleMecanumDrive.getVelocityConstraint(12, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(60))
                            .waitSeconds(1.5)
                            .lineToLinearHeading(new Pose2d(xStart - 16, yStart + 0, headingStart + Math.toRadians(0)), SampleMecanumDrive.getVelocityConstraint(10, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(60))
                            .lineToLinearHeading(new Pose2d(xStart - 4, yStart + 0.5, headingStart), SampleMecanumDrive.getVelocityConstraint(10, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(60))
                            .lineToLinearHeading(new Pose2d(xStart - 1, yStart + 36, headingStart + Math.toRadians(-90)))
                            .lineToLinearHeading(new Pose2d(xStart + 7, yStart + 36, headingStart + Math.toRadians(-90)))
                            .waitSeconds(1.5)
                            .lineToLinearHeading(new Pose2d(xStart - 1, yStart + 36, headingStart + Math.toRadians(0)))
                            .lineToLinearHeading(new Pose2d(xStart - 22, yStart + 24, headingStart))
                            .build());
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
                            .lineToLinearHeading(new Pose2d(xStart, yStart + 5.5,headingStart + Math.toRadians(-36)))
                            .lineToLinearHeading(new Pose2d(xStart - 18, yStart + 5.5, headingStart))
                            .lineToLinearHeading(new Pose2d(xStart -20, yStart + 5.5, headingStart), SampleMecanumDrive.getVelocityConstraint(12, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(90))
                            .lineToLinearHeading(new Pose2d(xStart - 16, yStart, headingStart + Math.toRadians(-10)), SampleMecanumDrive.getVelocityConstraint(5, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(90))
                            .lineToLinearHeading(new Pose2d(xStart - 0, yStart + 0.5, headingStart), SampleMecanumDrive.getVelocityConstraint(10, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(90))
                            .lineToLinearHeading(new Pose2d(xStart + 15, yStart + 25.5, headingStart + Math.toRadians(-39)))
                            .back(23)
                            .lineToLinearHeading(new Pose2d(xStart - 20, yStart + 31, headingStart + Math.toRadians(0)))
                            .build());


//                     .lineToLinearHeading(new Pose2d(25.5, -15, Math.toRadians(-38)))
//                    .lineToLinearHeading(new Pose2d(5.5,0, Math.toRadians(-36)))
//                    .lineToLinearHeading(new Pose2d(5.5, 18))
//                    .lineToLinearHeading(new Pose2d(5.5,20),SampleMecanumDrive.getVelocityConstraint(12 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
//                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
//                    .lineToLinearHeading(new Pose2d(0,16, Math.toRadians(-10)),SampleMecanumDrive.getVelocityConstraint(5 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
//                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL)).build();
//                                        .lineToLinearHeading(new Pose2d(0.5,0),SampleMecanumDrive.getVelocityConstraint(10 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
//                    SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL)).build();
//                                                            .lineToLinearHeading(new Pose2d(25.5, -15, Math.toRadians(-38)))
//                    .back(23)
//                    .lineToLinearHeading(new Pose2d(31, 20))




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
                            .lineToLinearHeading(new Pose2d(xStart, yStart + 5.5,headingStart + Math.toRadians(-36)))
                            .lineToLinearHeading(new Pose2d(xStart - 18, yStart + 5.5, headingStart))
                            .lineToLinearHeading(new Pose2d(xStart -20, yStart + 5.5, headingStart), SampleMecanumDrive.getVelocityConstraint(12, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(90))
                            .lineToLinearHeading(new Pose2d(xStart - 16, yStart, headingStart + Math.toRadians(-10)), SampleMecanumDrive.getVelocityConstraint(5, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(90))
                            .lineToLinearHeading(new Pose2d(xStart - 0, yStart + 0.5, headingStart), SampleMecanumDrive.getVelocityConstraint(10, 4.95, 12.07), SampleMecanumDrive.getAccelerationConstraint(90))
                            .lineToLinearHeading(new Pose2d(xStart + 15, yStart + 26, headingStart + Math.toRadians(-39)))
                            .lineToLinearHeading(new Pose2d(xStart -2, yStart + 3, headingStart + Math.toRadians(-36)))
                            .lineToLinearHeading(new Pose2d(xStart - 20, yStart + 31, headingStart + Math.toRadians(0)))
                            .build());




//            Trajectory dropBlock = drive.trajectoryBuilder(startPose)
//                    .lineToLinearHeading(new Pose2d(24.50, -14.25, Math.toRadians(-40)))
//                    .build();
//            Trajectory goBack = drive.trajectoryBuilder(dropBlock.end())
//                    .lineToLinearHeading(new Pose2d(5.5,0, Math.toRadians(-36)))
//                    .build();
//            Trajectory partCarousel = drive.trajectoryBuilder(goBack.end())
//                    .lineToLinearHeading(new Pose2d(5.5, 18))
//                    .build();
//            Trajectory carousel = drive.trajectoryBuilder(partCarousel.end())
//                    .lineToLinearHeading(new Pose2d(5.5,20),SampleMecanumDrive.getVelocityConstraint(12 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
//                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
//                    .build();
//            Trajectory pickDuck = drive.trajectoryBuilder(carousel.end())
//                    .lineToLinearHeading(new Pose2d(0,16, Math.toRadians(-10)),SampleMecanumDrive.getVelocityConstraint(5 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
//                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL)).build();
//            Trajectory moreIntake = drive.trajectoryBuilder(pickDuck.end())
//                    .lineToLinearHeading(new Pose2d(0.5,0),SampleMecanumDrive.getVelocityConstraint(10 , DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
//                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL)).build();
//            Trajectory dropBlock2 = drive.trajectoryBuilder(moreIntake.end())
//                    .lineToLinearHeading(new Pose2d(26, -15, Math.toRadians(-39)))
//                    .build();
//            Trajectory backten = drive.trajectoryBuilder(dropBlock2.end())
//                    .lineToLinearHeading(new Pose2d(3, 2, Math.toRadians(-36)))
//                    .build();
//            Trajectory park = drive.trajectoryBuilder(backten.end())
//                    .lineToLinearHeading(new Pose2d(31, 20))
//                    .build();

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
