/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

#include <frc/TimedRobot.h>
#include <frc/smartdashboard/smartdashboard.h>
#include <frc/PWMVictorSPX.h>
#include <frc/drive/DifferentialDrive.h>
#include <frc/util/color.h>
#include "rev/ColorSensorV3.h"
#include "rev/ColorMatch.h"

//#include "ctre/Phoenix.h"

/**
 * This is a simple example to show how the REV Color Sensor V3 can be used to
 * detect various colors.
 */
class Robot : public frc::TimedRobot {
  /**
   * Change the I2C port below to match the connection of your color sensor
   */
  static constexpr auto i2cPort = frc::I2C::Port::kOnboard;
  int compte_tour = 0;
  int stop_moteur = 1; // quand stop est 0, moteur fonctionne, quand stop est 1, moteur fonctionne pas,
  int coul_precedente = 0;

    //TalonSRX srx = {0};

   frc::PWMVictorSPX motor_ColorWheel{0};

  /**
   * A Rev Color Sensor V3 object is constructed with an I2C port as a 
   * parameter. The device will be automatically initialized with default 
   * parameters.
   */
  rev::ColorSensorV3 m_colorSensor{i2cPort};

  /**
   * A Rev Color Match object is used to register and detect known colors. This can 
   * be calibrated ahead of time or during operation.
   * 
   * This object uses a simple euclidian distance to estimate the closest match
   * with given confidence range.
   */
  rev::ColorMatch m_colorMatcher;

  /**
   * Note: Any example colors should be calibrated as the user needs, these
   * are here as a basic example.
   */
  static constexpr frc::Color kBlueTarget = frc::Color(0.143, 0.427, 0.429);
  static constexpr frc::Color kGreenTarget = frc::Color(0.197, 0.561, 0.240);
  static constexpr frc::Color kRedTarget = frc::Color(0.561, 0.232, 0.114);
  static constexpr frc::Color kYellowTarget = frc::Color(0.361, 0.524, 0.113);

  // Rev Color threshold
  // blue 0.143, 0.427, 0.429
  // green 0.197, 0.561, 0.240
  // red 0.561, 0.232, 0.114
  // yellow 0.361, 0.524, 0.113

 public:
  void RobotInit() {
    m_colorMatcher.AddColorMatch(kBlueTarget);
    m_colorMatcher.AddColorMatch(kGreenTarget);
    m_colorMatcher.AddColorMatch(kRedTarget);
    m_colorMatcher.AddColorMatch(kYellowTarget);

   // srx.Set(ControlMode::PercentOutput, 0);
  }
  void RobotPeriodic() {
    /**
     * The method GetColor() returns a normalized color value from the sensor and can be
     * useful if outputting the color to an RGB LED or similar. To
     * read the raw color, use GetRawColor().
     * 
     * The color sensor works best when within a few inches from an object in
     * well lit conditions (the built in LED is a big help here!). The farther
     * an object is the more light from the surroundings will bleed into the 
     * measurements and make it difficult to accurately determine its color.
     */
    frc::Color detectedColor = m_colorSensor.GetColor();


    std::string motorString;
    /**
     * Run the color match algorithm on our detected color
     */
    std::string colorString;
    double confidence = 0.0;
    frc::Color matchedColor = m_colorMatcher.MatchClosestColor(detectedColor, confidence);

    if (matchedColor == kBlueTarget || matchedColor == kGreenTarget || matchedColor == kRedTarget || matchedColor == kYellowTarget){
        stop_moteur = 0;
        std::cout << "Begin motor \n";
    }

    if (matchedColor == kBlueTarget) {
      colorString = "Blue";
      coul_precedente = 1;
      std::cout << "Blue \n";
    } else if (matchedColor == kGreenTarget) {
      colorString = "Green";
      std::cout << "Green \n";

       if (coul_precedente != 2){
          compte_tour++;
          coul_precedente = 2;
        }

    } else if (matchedColor == kRedTarget) {
      colorString = "Red"; 
      coul_precedente = 3;
      std::cout << "Red \n";    
    } else if (matchedColor == kYellowTarget) {
      colorString = "Yellow";
      std::cout << "Yellow \n";
      coul_precedente = 4;
    } else {
      colorString = "Unknown";
      std::cout << "Unknown \n";
    }
    

     switch (compte_tour)
      {
        case 2:
          std::cout << "1 TOUR \n";
          break;
         case 4:
         std::cout << "2 TOUR \n";
          break;
         case 6:
          std::cout << "3 TOUR \n";
          std::cout << "Stop motor \n";
          stop_moteur = 1;
          break;     
      }

      if (stop_moteur == 0){
          motorString = "ON";  
      // Drive robot
      motor_ColorWheel.Set(1.0);
      }
      else if(stop_moteur == 1){
          motorString = "OFF";
      // Stop robot
      motor_ColorWheel.Set(0.0);
      }

    /**
     * Open Smart Dashboard or Shuffleboard to see the color detected by the 
     * sensor.
     */
    frc::SmartDashboard::PutNumber("Red", detectedColor.red);
    frc::SmartDashboard::PutNumber("Green", detectedColor.green);
    frc::SmartDashboard::PutNumber("Blue", detectedColor.blue);
    frc::SmartDashboard::PutNumber("Confidence", confidence);
    frc::SmartDashboard::PutString("Detected Color", colorString);
    frc::SmartDashboard::PutNumber("Tour", compte_tour);
    frc::SmartDashboard::PutString("Motor", motorString);




  }
};

#ifndef RUNNING_FRC_TESTS
int main() { return frc::StartRobot<Robot>(); }
#endif