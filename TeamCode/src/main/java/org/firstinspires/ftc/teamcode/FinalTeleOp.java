package org.firstinspires.ftc.teamcode;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.internal.OptimizedController;
import org.firstinspires.ftc.internal.OptimizedRobot;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.examples.SampleControllerMapping;

@TeleOp(name = "Final TeleOP")
public class FinalTeleOp extends OpMode {
    DcMotor duckSpinner, arm;
    OptimizedRobot robot;
    OptimizedController controller, controller2;
    CRServo leftServo, rightServo;
    Telemetry.Log log;

    @Override
    public void init() {
        robot = new OptimizedRobot(telemetry, hardwareMap, new SampleControllerMapping());
        duckSpinner = robot.getMotor("duckSpinner");
        arm = robot.getMotor("arm");
        leftServo = hardwareMap.crservo.get("Left Servo");
        rightServo = hardwareMap.crservo.get("Right Servo");
        controller = new OptimizedController(gamepad1);
        controller2 = new OptimizedController(gamepad2);
        log = telemetry.log();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void loop() {
        if (controller2.getBool(OptimizedController.Key.A)) {
            duckSpinner.setPower(-0.6);
        } else {
            duckSpinner.setPower(0);
        }
        robot.updateDrive(controller, controller2, true, false, 1d, OptimizedRobot.RobotDirection.FRONT, OptimizedRobot.RobotDirection.FRONT, false);
        arm.setPower(Math.pow(controller2.getFloat(robot.getControl("Arm")), 3));
        leftServo.setPower(controller2.getToggle(robot.getControl("Claw")) ? 0.5: -0.5);
        rightServo.setPower(controller2.getToggle(robot.getControl("Claw")) ? -0.5: 0.5);
    }
}
