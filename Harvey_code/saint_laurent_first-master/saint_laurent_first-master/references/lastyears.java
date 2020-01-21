/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.JoystickBase;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.cameraserver.CameraServer;



/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  //////////////Joystick//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    Joystick JoystickControleur = new Joystick(0);
    Joystick ManettePilote = new Joystick(1);

    /////////Drive///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    PWMVictorSPX moteurDriveDroite = new PWMVictorSPX(0);
    PWMVictorSPX moteurDriveGauche = new PWMVictorSPX(1);
    DifferentialDrive drive = new DifferentialDrive(moteurDriveGauche, moteurDriveDroite);
    
    Double rapportVitesse = 0.4;
    Boolean inversionDrive = true;

    ////////BRAS//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    AnalogInput potBras = new AnalogInput(0);  //Degré  degrées avec le poteau vertical 
    PWMVictorSPX moteurBras = new PWMVictorSPX(3);
    Double offsetBras = 128.0;//143

    double valJoy;
    double inversion =360.0;
    double ValeurJoystick;
    double rapportGravitationnel;

    double anglePot;
    double vitessePelle = 0.5;
    double offset = -137;;
    double kSoutienBras;// Regarder la puissance à appliquer au moteur lorsqu'il est à 90 degrés
    double angleRotationMaximum;//Angle lorsque la pelle est en haut complètement 
    double angleRotationMinimum;// Angle lorsque la pelle est en bas complètement
    double angleRalentissement;
    double alJoy;
    double longeurHorizontale;
    double sensibilite;  //Slider sur le joystick
    double soutienBras;
    double deplacementBras;
    double vitesse;
    double sensibiliteBras;
    double soutienBrasABS;
    double deplacementBrasABS;

    

    /////////PELLE//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    Ultrasonic ultraPelle = new Ultrasonic(0, 1 );
    PWMVictorSPX moteurPelle = new PWMVictorSPX(2);
    double currentDistance;

    //////////////PNEUMATIQUE////////////////////////////////////////////////////////////////////////////////////////////////////////

    DoubleSolenoid pistonDescendre = new DoubleSolenoid(4,5);
    DoubleSolenoid pistonSuction = new DoubleSolenoid(2,3);
    DoubleSolenoid pistonTranslation = new DoubleSolenoid(0,1);

    Timer msPiston = new Timer();

    Compressor c = new Compressor(); 

    DigitalInput switchVentouse = new DigitalInput(2);  
    
    public static boolean etatPistonDescendre = false;
    public static boolean etatPistonSuction = false;
    public static boolean etatPistonTranslation = false;
    public static boolean etatCompresseur = true;

    ///Variables pour commandes automatique
    public static double tempsProchaineSuction = -1;
    public static double tempsProchaineDescente = -1;
    public static double tempsProchaineTranslationAvant = -1;
    public static double tempsProchaineDesuction = -1;
    public static double tempsProchaineRemontee = -1;
    public static double tempsProchaineTranslationArriere = -1;

    public static double tempsPiston;


    ////Système d'élévation du robot//////

    PWMVictorSPX mAvantDroit = new PWMVictorSPX(4);
    PWMVictorSPX mAvantgauche = new PWMVictorSPX(5);

    DigitalInput LSAVDL = new DigitalInput(11);
    DigitalInput LSAVDH = new DigitalInput(10);
    DigitalInput LSAVGL = new DigitalInput(13);
    DigitalInput LSAVGH = new DigitalInput(12);

    double vitesseMoteurAvantDroit = 0;
    double vitesseMoteurAvantGauche = 0;

    
    @Override
    public void robotInit() {
      ultraPelle.setAutomaticMode(true);                    //Acticer détecteur de balon
      CameraServer.getInstance().startAutomaticCapture();   //Activer la caméra
      c.setClosedLoopControl(true); 
      msPiston.start();
    }

  @Override
  public void robotPeriodic() {

    //Section pour le debug
    /*
    SmartDashboard.putNumber("AnglePelle", anglePot);
    SmartDashboard.putNumber("Sensibilite Pelle", sensibilite);
    SmartDashboard.putBoolean("Hatch", switchVentouse.get());
    
    anglePot = ((0.065*potBras.getValue())+offset);
    System.out.println("AnglePelle :" + anglePot);
    SmartDashboard.putNumber("valJoy", valJoy);
    SmartDashboard.putNumber("Sensibilite Pelle", sensibilite);
    SmartDashboard.putNumber("VitesseMoteur", vitessePelle);
    SmartDashboard.putNumber("LargeurHorizontale", longeurHorizontale);
    SmartDashboard.putNumber("DeplacementBras", deplacementBras);
    SmartDashboard.putNumber("SoutienBras", soutienBras);
    System.out.println("Distance :" + currentDistance);
*/
  }

    
    @Override
    public void autonomousInit() {
      c.setClosedLoopControl(true);
    }

    /**
     * This function is called periodically during autonomous.
     */
    @Override
    public void autonomousPeriodic() {
      ControllerBras();
      ActiverPelle();
      Pneumatique();
      Drive();
    }

  public void teleopInit(){
  }
  
  public void teleopPeriodic() {
    ControllerBras();
    ActiverPelle();
    Pneumatique();
    Drive();  
    //MonterRobot();
  } 

  public void MonterRobot(){

    //    Sortir Piston : 180
    //    Entrer Piston : 0 

    if(JoystickControleur.getRawButton(6))//Monter
    {
      vitesseMoteurAvantDroit = -0.;
      vitesseMoteurAvantGauche = -0.;
    }
    else if(JoystickControleur.getRawButton(4)) //Descendre
    {
      vitesseMoteurAvantGauche = 0.5;
      vitesseMoteurAvantDroit = 0.5;
    }
    else {                                  //Arrêter
      vitesseMoteurAvantDroit = 0;
      vitesseMoteurAvantGauche = 0;
    }

    ///Limit Switchs //
    /*
    if(LSAVDH.get()==false && vitesseMoteurAvantDroit < 0 ){ //Coté Droit Haut
      vitesseMoteurAvantDroit = 0;
      System.out.println("LSAVDH pin 1 DIO11");
    }

    if(LSAVGH.get()==false && vitesseMoteurAvantGauche < 0){// Cote Gauche Haut
    vitesseMoteurAvantGauche = 0;
    System.out.println("LSAVGH pin 3 DIO13");   
    }

    if(LSAVDL.get()==false && vitesseMoteurAvantDroit > 0 ){ //Coté droit bas
      vitesseMoteurAvantDroit = 0;
      System.out.println("LSAVDL pin 0 DIO10");
    }

    if(LSAVGL.get()==false && vitesseMoteurAvantGauche > 0 ){ // Coté Gauche bas
    vitesseMoteurAvantGauche = 0;
    System.out.println("LSAVGL pin 2 DIO12");   
    }

    SmartDashboard.putNumber("PistonDroit", vitesseMoteurAvantDroit);
    SmartDashboard.putNumber("PistonGauche", vitesseMoteurAvantGauche);
    */
   mAvantDroit.setSpeed(vitesseMoteurAvantDroit);
   mAvantgauche.setSpeed(vitesseMoteurAvantGauche);
  }

  public void Drive(){

    if (ManettePilote.getRawButtonPressed(6) == true && rapportVitesse < 0.8)  
    {//Front montant
      rapportVitesse += 0.20;
      SmartDashboard.putNumber("Rapport vitesse", rapportVitesse*100);
    }
    if (ManettePilote.getRawButtonPressed(5) == true && rapportVitesse > 0.40)
    {//Front montant
      rapportVitesse -= 0.20;
      SmartDashboard.putNumber("Rapport vitesse", rapportVitesse*100);
    }

    /*if(ManettePilote.getRawButton(9) && ManettePilote.getRawButton(10) && ((Joystick.getRawAxis(1)>0.75 && Joystick.getRawAxis(5)>0.75) || (Joystick.getRawAxis(1) < -0.75 && Joystick.getRawAxis(5)>-0.75)))
    rapportVitesse = 1.0;*/

    

    if(ManettePilote.getRawButtonReleased(9) && rapportVitesse == 1.0){
      rapportVitesse = 0.40;
    }

    if(ManettePilote.getRawButtonPressed(7)){
      inversionDrive = true;
      System.out.println( "Hatch");
    }
    if(ManettePilote.getRawButtonPressed(8)){
      inversionDrive = false;
      System.out.println("Pelle");
    }

   if(ManettePilote.getPOV() == 0){
    drive.tankDrive(1.0, 1.0);
   }
   else if(ManettePilote.getPOV() == 180){
    drive.tankDrive(-1.0, -1.0);
   }
   else{ 
    if (inversionDrive = true)
    drive.tankDrive(ManettePilote.getRawAxis(5)*rapportVitesse, ManettePilote.getRawAxis(1)*rapportVitesse);
    if (inversionDrive = false)
    drive.tankDrive(-ManettePilote.getRawAxis(1)*rapportVitesse, -ManettePilote.getRawAxis(5)*rapportVitesse);
    
    }
  }
  public void ActiverPelle (){
    
        System.out.print(ManettePilote.getRawAxis(2));

        if(JoystickControleur.getRawButton(7) || ManettePilote.getRawButton(2) ){
          moteurPelle.setSpeed(1.0); //Expulser
          
        }
        else  if(JoystickControleur.getRawButton(8) || ManettePilote.getRawButton(1)){ //&& currentDistance < 7
          currentDistance = ultraPelle.getRangeInches();
            if( (currentDistance > 5.8 || currentDistance < 5.3))
            moteurPelle.setSpeed(-0.4);//Aspirer  
          }

          else if(JoystickControleur.getRawButton(9) || ManettePilote.getRawAxis(3)> 0.6 ||  ManettePilote.getRawAxis(2)> 0.6){
            moteurPelle.setSpeed(0.5); //Expulser medium
    
          }
          else{
            moteurPelle.setSpeed(0);
          } 
    /*
         if(currentDistance > 7 || currentDistance <5){
          SmartDashboard.putBoolean("Balon",false); //Afficher qu'il y a un balon dans la pelle
         }
         else{
          SmartDashboard.putBoolean("Balon",true); //Afficher qu'il n'y a pas de balon dans le pelle
         }
          */
  }

  public void Pneumatique(){
    //\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\Commandes pneumatique simples\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    ///////////Extension de ventouse////////////////////////
              if (JoystickControleur.getRawButtonPressed(5))
                etatPistonDescendre = !etatPistonDescendre;       
            
    ////////////Descendre, monter Ventouse /////////////////
              if (JoystickControleur.getRawButtonPressed(1))
              etatPistonSuction = !etatPistonSuction;
              
    /////////////interrupteur///////////////////////////////
              if(switchVentouse.get() == false)
                etatPistonSuction = false;          

    //////////////Avancer/Reculer Ventouse///////////////////
              if (JoystickControleur.getRawButtonPressed(2))
              etatPistonTranslation = !etatPistonTranslation;

    ///////////////Compresseur /////////////////////////////
        /*  if (JoystickControleur.getRawButtonPressed(11)){
            etatCompresseur = !etatCompresseur;
            System.out.println("Compresseur");
          }
    */              
              
    //\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\Commandes pneumatique avancées\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    tempsPiston = msPiston.get(); //Temps pour faire des commandes

    //Avancer les suces et faire une suction/////////////////
          if(JoystickControleur.getRawButtonPressed(3)){         
            tempsProchaineSuction = tempsPiston + 2;
            tempsProchaineTranslationAvant = tempsPiston;
            System.out.println("Commande 4");
          }

    ////Actualisation des états avec les délais//////////////////////
    if(tempsProchaineSuction !=-1 && tempsPiston < tempsProchaineSuction ){
    etatPistonSuction = true;
    tempsProchaineSuction = -1;
    }

    if(tempsProchaineDesuction !=-1 && tempsPiston < tempsProchaineDesuction ){
    etatPistonSuction = false;
    tempsProchaineDesuction = -1;
    }

    if(tempsProchaineDescente !=-1 && tempsPiston < tempsProchaineDescente ){
    etatPistonDescendre = true;
    tempsProchaineDescente = -1;
    }

    if(tempsProchaineRemontee !=-1 && tempsPiston < tempsProchaineRemontee ){
    etatPistonDescendre = false;
    tempsProchaineRemontee = -1;
    }

    if(tempsProchaineTranslationAvant !=-1 && tempsPiston < tempsProchaineTranslationAvant ){
    etatPistonTranslation = true;
    tempsProchaineTranslationAvant =-1;
    }

    if(tempsProchaineTranslationArriere !=-1 && tempsPiston < tempsProchaineTranslationArriere){
    etatPistonTranslation = false;
    tempsProchaineTranslationArriere = -1;
    }
    
/////////////////////////////////////////Actualisation des états des pistons et du compresseur\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    if(etatPistonDescendre == true)
      pistonDescendre.set(DoubleSolenoid.Value.kForward);
    else{
     pistonDescendre.set(DoubleSolenoid.Value.kReverse);
   }
    if(etatPistonSuction == true){
     pistonSuction.set(DoubleSolenoid.Value.kForward);  
      SmartDashboard.putString("VENTOUSES", "SUCENT");
    }
    else{
     pistonSuction.set(DoubleSolenoid.Value.kReverse);
     SmartDashboard.putString("VENTOUSES", "NE SUCENT PAS");
    }

    if(etatPistonTranslation == true)
     pistonTranslation.set(DoubleSolenoid.Value.kForward);
    else
     pistonTranslation.set(DoubleSolenoid.Value.kReverse);

    c.setClosedLoopControl(etatCompresseur);
    SmartDashboard.putBoolean("COMPRESSEUR", etatCompresseur);
    SmartDashboard.putBoolean("ETAT VENTOUSES", etatPistonSuction);

  }

  public void ControllerBras(){


    //-135;
    kSoutienBras = 0.3;// inscrire la puissance à appliquer au moteur lorsqu'il est à 90 degrés
    angleRotationMaximum = 129;//Angle lorsque la pelle est en haut complètement 
    angleRotationMinimum = -33;// Angle lorsque la pelle est en bas complètement
    angleRalentissement = 0;

    valJoy= -JoystickControleur.getRawAxis(1);

    anglePot = ((0.065*potBras.getValue())+offset);

    longeurHorizontale = (Math.cos(Math.toRadians(anglePot)));

    sensibilite = ((1-JoystickControleur.getRawAxis(3))/2); //Slider sur le joystick

    soutienBras     = kSoutienBras * longeurHorizontale;          //Ce qui permet de retenir la pelle

    deplacementBras = (valJoy * longeurHorizontale*sensibilite) + valJoy* 0.4;  //Ce qui permet de déplacer la pelle

    deplacementBrasABS = Math.abs(deplacementBras);

    if(angleRotationMaximum > anglePot && angleRotationMinimum < anglePot) // Si le bras est à l'interrieur des angles limites
    soutienBrasABS = Math.abs(soutienBras); // on met un soutien au bras pour qu'il s'imobilise quand il est dans les airs
    else
    soutienBrasABS = 0; //Sinon on enlève le soutien du bras pour ne pas que les moteurs travaillent pour rien lorsque la pelle est sur les bumper en caoutchouc aux limites

    // vitesse Monter Positif ou negatif
    //Pour monter la pelle c'est  positif
    //Pour descendre la pelle, negatif
    
    if(valJoy < -0.01){ //Si on monte
      if(anglePot < 90 ) //AAAA//Et qu'on est en bas de 90 degrés
      {
        vitessePelle = +soutienBrasABS + deplacementBrasABS ;
      }
      if(anglePot >= 90) //BBBB//Et qu'on est en haut que 90 degrés
      {
        vitessePelle = -soutienBrasABS + deplacementBrasABS;
        System.out.println(deplacementBras);
      }
    }

    else if(valJoy > 0.01){//Si on descend
      if(anglePot < 90) //AAAA//Et qu'on est en bas de 90 degrés
      {
      vitessePelle = +soutienBrasABS - deplacementBrasABS;
      }
      if(anglePot >= 90) //BBBB //Et qu'on est en haut que 90 degrés
      {
      vitessePelle = -soutienBrasABS -deplacementBrasABS;
      }
    }
    else{
      if(anglePot < 90) //AAAA//Et qu'on est en bas de 90 degrés
      {
        vitessePelle = +soutienBrasABS;
      }
      if(anglePot >= 90) //BBBB//Et qu'on est en haut que 90 degrés
      {
        vitessePelle = -soutienBrasABS;
      }
    }
    System.out.println(vitessePelle);
    moteurBras.setSpeed(vitessePelle);
  }
}
