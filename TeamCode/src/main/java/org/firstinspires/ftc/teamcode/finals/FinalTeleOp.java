package org.firstinspires.ftc.teamcode.finals;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.internal.OptimizedController;
import org.firstinspires.ftc.internal.OptimizedRobot;
import org.firstinspires.ftc.internal.RobotConfig;
import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name = "Final TeleOP")
public class FinalTeleOp extends OpMode {
    DcMotor duckSpinner, arm, odometry, intake;
    OptimizedRobot robot;
    OptimizedController controller1, controller2;
    ColorSensor colorSensor;
    Telemetry.Log log;
    int armStart = -3200;
    int frontHighOuttake = -2220;
    int backHighOuttake = -900;
    int frontLowOuttake = -2000;
    int backLowOuttake = -450;

    @Override
    public void init() {
        controller1 = new OptimizedController(gamepad1);
        controller2 = new OptimizedController(gamepad2);

        log = telemetry.log();

        robot = new OptimizedRobot(controller1, controller2, telemetry, hardwareMap, new FreightFrenzyControllerMapping());
        duckSpinner = robot.getMotor("duckSpinner");
        arm = robot.getMotor("arm", DcMotor.RunMode.RUN_TO_POSITION);
        odometry = robot.getMotor("odometry");
        intake = robot.getMotor("intake");

        colorSensor = hardwareMap.colorSensor.get("colorSensor");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void loop() {
        // Duck Spinner
        if (robot.getControl("Duck")) {
            duckSpinner.setPower(RobotConfig.SPINNER_SPEED);
        } else {
            duckSpinner.setPower(-robot.getControlFloat("DuckSpeed"));
        }

        // If Arm Is Being Manually Controlled
        if (robot.getControl("ArmControl")) {
            // Set Arm's Mode to Manual Control
            arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            // Set Power to "ArmSmooth" Float
            arm.setPower(Math.pow(robot.getControlFloat("ArmSmooth"), 3));
        } else {
            // Set Arm's Mode to Automatically Move To Position
            arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            // Set Arm's Power to full
            arm.setPower(RobotConfig.ARM_POWER);
            // If Arm is in Outtake Mode
            if (!robot.getControl("ArmIntake")) {
                // If Arm's Outtake Mode is the Front
                if (robot.getControl("ArmSide")) {
                    // Set to Front High Outtake if ArmHeight is true, otherwise to to Front Low Outtake
                    arm.setTargetPosition(robot.getControl("ArmHeight") ? frontLowOuttake : frontHighOuttake);
                } else {
                    // Set to Back High Outtake if ArmHeight is true, otherwise to to Back Low Outtake
                    arm.setTargetPosition(robot.getControl("ArmHeight") ?  backLowOuttake: backHighOuttake);
                }
            } else {
                // Move Arm to Intake Position
                arm.setTargetPosition(armStart);
            }
        }

        // If IntakeButton is pressed
        if (robot.getControl("IntakeButton")) {
            // Intake with full power
            intake.setPower(1);
        } else if (robot.getControl("OuttakeButton")) {
            // Outtake with full power
            intake.setPower(-1);
        } else {
            // Set Intake/Outtake Power to Intake Float - Outtake Float
            intake.setPower(robot.getControlFloat("Intake") - robot.getControlFloat("Outtake"));
        }

        colorSensor.enableLed(true);
        log.add(String.valueOf(colorSensor.green()>1000));

        // Set odometry power to the left stick cubed, to add mild curving
        odometry.setPower(Math.pow(robot.getControlFloat("Odometry"), 3));
        log.add("Arm Position: " + arm.getCurrentPosition());
        log.clear();

        // Main drive method
        robot.updateDrive(controller1, controller2, true, false, 0.7d, OptimizedRobot.RobotDirection.FRONT, OptimizedRobot.RobotDirection.FRONT, false);
    }
}