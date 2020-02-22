/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

#include <cameraserver/CameraServer.h>
#include <frc/TimedRobot.h>
#include <wpi/raw_ostream.h>
#include <frc/SerialPort.h>
//#include <WPISerialPort.h>


#define BAUDRATE 115200
/**
 * Uses the CameraServer class to automatically capture video from a USB webcam
 * and send it to the FRC dashboard without doing any vision processing. This is
 * the easiest way to get camera images to the dashboard. Just add this to the
 * RobotInit() method in your program.
 */
class Robot : public frc::TimedRobot {
 public:
  void RobotInit() override {
#if defined(__linux__)
    frc::CameraServer::GetInstance()->StartAutomaticCapture();

#else
    wpi::errs() << "Vision only available on Linux.\n";
    wpi::errs().flush();
  #endif

frc::SerialPort::Port com = frc::SerialPort::Port::kUSB;
frc::SerialPort::Parity parite =frc::SerialPort::Parity::kParity_None;
frc:: SerialPort::StopBits stopb = frc:: SerialPort::StopBits::kStopBits_One;

//frc::SerialPort::Port port = frc::SerialPort::Port::kUSB;
//frc::SerialPort::Parity parity =frc::SerialPort::Parity::kParity_None;
//frc:: SerialPort::StopBits stopbBits = frc:: SerialPort::StopBits::kStopBits_One;



frc::SerialPort(115200, com, 8,  parite, stopb);
  
//frc::SerialPort::SerialPort(115200, frc::SerialPort::Port port = frc::SerialPort::Port::kUSB, 8, frc::SerialPort::Parity parity =frc::SerialPort::Parity::kParity_None, frc:: SerialPort::StopBits stopbBits = frc:: SerialPort::StopBits::kStopBits_One);

// Il me reste à read le serial et à le print sur la console.
// voici le lien pour la librairie SerialPort.h pour savoir comment read un serial
//https://first.wpi.edu/FRC/roborio/release/docs/cpp/classfrc_1_1SerialPort.html#a25b0be1bf26f74a96e2f5de5bf073c6e


  }
};

#ifndef RUNNING_FRC_TESTS
int main() { return frc::StartRobot<Robot>(); }
#endif
