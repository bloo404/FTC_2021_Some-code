/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import static org.firstinspires.ftc.teamcode.Hardware_Bistrita.PRINDERE_COMPLETA;
import static org.firstinspires.ftc.teamcode.Hardware_Bistrita.PRINDERE_INITIAL;
import static org.firstinspires.ftc.teamcode.Hardware_Bistrita.TAVA_JOS;
import static org.firstinspires.ftc.teamcode.Hardware_Bistrita.TAVA_SUS;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all iterative OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Bistrita_Teleop", group="Iterative Opmode")
//@Disabled
public class Bistrita_TeleOp extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    Hardware_Bistrita robot = new Hardware_Bistrita();
    double poz_tava=TAVA_SUS;
    double poz_prindere=PRINDERE_INITIAL;
    //PENTRU MOTOARELE DE DEPLASARE
    double LF,LB,RF,RB=0;
    //PENTRU JOYSTICK
    double x1,y1,x2,y2;
    //operational stuff
    double joyScale = 0.5;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Before Initialization");
        robot.init(hardwareMap);
        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        // Setup a variable for each drive wheel to save power level for telemetry

        /** DEPLASARE MECANUM **/

        double leftPower;
        double rightPower;

        double drive = -gamepad1.left_stick_y;
        double turn  =  gamepad1.right_stick_x;
        double strafe_right = gamepad1.right_trigger;
        double strafe_left = gamepad1.left_trigger;

        leftPower    = Range.clip(drive + turn, -1.0, 1.0) ;
        rightPower   = Range.clip(drive - turn, -1.0, 1.0) ;


        if (strafe_right>0)
        {
            robot.LeftBackMotor.setPower(-strafe_right);
            robot.LeftFrontMotor.setPower(strafe_right);
            robot.RightBackMotor.setPower(strafe_right);
            robot.RightFrontMotor.setPower(-strafe_right);
        }
        else
            if (strafe_left>0)
            {
                robot.LeftBackMotor.setPower(strafe_left);
                robot.LeftFrontMotor.setPower(-strafe_left);
                robot.RightBackMotor.setPower(-strafe_left);
                robot.RightFrontMotor.setPower(strafe_left);
            }
            else {
                robot.LeftBackMotor.setPower(leftPower);
                robot.LeftFrontMotor.setPower(leftPower);
                robot.RightFrontMotor.setPower(rightPower);
                robot.RightBackMotor.setPower(rightPower);
            }

        /** SFARSIT DEPLASARE **/

        /** SLIDERE BRAT **/

        double armPower=0;

        if (gamepad2.a==true) ///retragere slidere
            armPower = gamepad2.left_trigger;
        else
        if (gamepad2.b) ///extindere slidere
            armPower = -gamepad2.left_trigger;


        double verticalPower=0;

        if (gamepad2.a==true) ///sus || jos
            verticalPower = gamepad2.right_trigger;
        else
        if (gamepad2.b==true) /// jos || sus
            verticalPower = -gamepad2.right_trigger;

        robot.SliderMotor.setPower(armPower);
        robot.VerticalMotor.setPower(verticalPower);


        if (gamepad2.dpad_left)
            poz_prindere= PRINDERE_COMPLETA;
        else
            if (gamepad2.dpad_right)
                poz_prindere=PRINDERE_INITIAL;

        robot.prindere.setPosition(poz_prindere);

        /** SFARSIT SLIDERE BRAT **/

        /** TRAGERE TAVA **/

        double jos;
        if (gamepad1.x)
            jos=1;
        else
            if (gamepad1.y)
                jos=-1;
            else
                jos=0;
        robot.PullMotor.setPower(jos);

        if (gamepad1.dpad_up)
            poz_tava=PRINDERE_COMPLETA;
        else
            if (gamepad1.dpad_down)
                poz_tava=PRINDERE_INITIAL;


        robot.tava.setPosition(poz_tava);

        /** SFARSIT TRAGERE TAVA **/

        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        robot.LeftBackMotor.setPower(0);
        robot.LeftFrontMotor.setPower(0);
        robot.RightFrontMotor.setPower(0);
        robot.RightBackMotor.setPower(0);

        robot.SliderMotor.setPower(0);
        robot.VerticalMotor.setPower(0);
        robot.PullMotor.setPower(0);
    }

}
