//
//  ViewController.swift
//  Hack Attack
//
//  Created by Binit Shah on 1/21/17.
//  Copyright © 2017 Binit Shah. All rights reserved.
//

import UIKit
import AVFoundation
import FirebaseDatabase
import MapKit
import CoreLocation

class ViewController: UIViewController, CLLocationManagerDelegate {
    //Links
    @IBOutlet weak var cameraView: UIView!
    @IBOutlet weak var overlayView: UIView!
    @IBOutlet weak var joinButton: UIButton!
    @IBOutlet weak var hostButton: UIButton!
    @IBOutlet weak var codeTextField: UITextField!
    @IBOutlet weak var codeLabel: UILabel!
    @IBOutlet weak var gameView: UIView!
    @IBOutlet weak var roleLabel: UILabel!
    @IBOutlet weak var playerLeftTextView: UITextView!
    @IBOutlet weak var runTextView: UITextView!
    @IBOutlet weak var runTimerLabel: UILabel!
    @IBOutlet weak var mainTimerLabel: UILabel!
    @IBOutlet weak var debugDistanceLabel: UILabel!
    @IBOutlet weak var preyDistanceLabel: UILabel!
    
    //In order to stream live video
    var captureSession : AVCaptureSession?
    var stillImageOutput : AVCaptureStillImageOutput?
    var previewLayer : AVCaptureVideoPreviewLayer?
    
    //Firebase
    var ref: FIRDatabaseReference!
    var finalCode: String?
    var playerNum: String?
    var gameTimer: Int = 0
    var hackingTimer: Int = 0
    
    //Location
    let locationManager = CLLocationManager()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        joinButton.layer.cornerRadius = 3
        hostButton.layer.cornerRadius = 3
        ref = FIRDatabase.database().reference()
        
        // For use in foreground
        self.locationManager.requestWhenInUseAuthorization()
        
        if CLLocationManager.locationServicesEnabled() {
            locationManager.delegate = self
            locationManager.desiredAccuracy = kCLLocationAccuracyBest
            locationManager.startUpdatingLocation()
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func playerGoingOffline() {
        print("Going Offline was called")
        if finalCode != nil {
            let gameRef: FIRDatabaseReference = ref.child("games").child(finalCode!)
            gameRef.child(playerNum!).setValue(false)
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        previewLayer?.frame = cameraView.bounds
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        captureSession = AVCaptureSession()
        captureSession?.sessionPreset = AVCaptureSessionPreset1920x1080
        
        let backCamera = AVCaptureDevice.defaultDevice(withMediaType: AVMediaTypeVideo)
        
        do {
            let input = try AVCaptureDeviceInput(device: backCamera)
            if(captureSession?.canAddInput(input) != nil) {
                captureSession?.addInput(input)
                
                stillImageOutput = AVCaptureStillImageOutput()
                stillImageOutput?.outputSettings = [AVVideoCodecKey : AVVideoCodecJPEG]
                
                if (captureSession?.canAddOutput(stillImageOutput) != nil){
                    captureSession?.addOutput(stillImageOutput)
                    
                    previewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
                    previewLayer?.videoGravity = AVLayerVideoGravityResizeAspect
                    previewLayer?.connection.videoOrientation = AVCaptureVideoOrientation.landscapeRight
                    cameraView.layer.addSublayer(previewLayer!)
                    captureSession?.startRunning()
                    
                }
            }
        }
        catch {
            print("exception!");
        }
    }
    
    @IBAction func joinButton(_ sender: UIButton) {
        if codeTextField.layer.isHidden == true {
            hostButton.layer.isHidden = true
            codeTextField.layer.isHidden = false
        }
        else {
            let inputtedCode: String = codeTextField.text!
            if inputtedCode.characters.count != 6 {
                showBoundsAlert()
            }
            else {
                let gameRef: FIRDatabaseReference = self.ref.child("games").child(inputtedCode);
                _ = gameRef.child("player1").observeSingleEvent(of: .value, with: { (snapshot) in
                    if let player1 = snapshot.value as? Bool {
                        if player1 != false {
                            gameRef.child("player2").setValue(true)
                            print("Player 1 is also active")
                            self.overlayView.removeFromSuperview()
                            gameRef.removeAllObservers()
                            self.finalCode = inputtedCode
                            self.playerNum = "player2"
                            
                            if Int(inputtedCode)! % 2 == 1 {
                                self.playAsPredator()
                            }
                            else {
                                self.playAsPrey()
                            }
                        }
                    }
                })
            }
        }
    }
    
    @IBAction func hostButton(_ sender: UIButton) {
        joinButton.layer.isHidden = true
        hostButton.layer.isHidden = true
        codeLabel.layer.isHidden = false
        
        let code: String = randomString(length: 6);
        let gameRef: FIRDatabaseReference = self.ref.child("games").child(code);
        gameRef.child("player1").setValue(true)
        codeLabel.text = "Lobby Code: " + code
        
        _ = gameRef.child("player2").observe(FIRDataEventType.value, with: { (snapshot) in
            if let player2 = snapshot.value as? Bool {
                if player2 != false {
                    print("Player 2 is also active")
                    self.overlayView.removeFromSuperview()
                    gameRef.removeAllObservers()
                    self.finalCode = code
                    self.playerNum = "player1"
                    
                    if Int(code)! % 2 == 1 {
                        self.playAsPrey()
                    }
                    else {
                        self.playAsPredator()
                    }
                }
            }
        })
    }
    
    func playAsPredator() {
        gameView.layer.isHidden = false
        roleLabel.text = "Predator"
        let opposingNum: String!
        if playerNum == "player1" {
            opposingNum = "player2"
        }
        else {
            opposingNum = "player1"
        }
        
        let gameRef: FIRDatabaseReference = self.ref.child("games").child(finalCode!);
        _ = gameRef.child(opposingNum).observe(FIRDataEventType.value, with: { (snapshot) in
            if let opposingPlayer = snapshot.value as? Bool {
                if opposingPlayer != false {
                    //play the game
                    _ = Timer.scheduledTimer(timeInterval: 2.0, target: self, selector: #selector(ViewController.timedLocationUpdate), userInfo: nil, repeats: true)
                }
                else {
                    //time to show the game ended because the other player left
                    self.playerLeftTextView.layer.isHidden = false
                }
            }
        })
    }
    
    func playAsPrey() {
        gameView.layer.isHidden = false
        roleLabel.text = "Prey"
        let opposingNum: String!
        if playerNum == "player1" {
            opposingNum = "player2"
        }
        else {
            opposingNum = "player1"
        }
        
        let gameRef: FIRDatabaseReference = ref.child("games").child(finalCode!)
        _ = gameRef.child(opposingNum).observe(FIRDataEventType.value, with: { (snapshot) in
            if let opposingPlayer = snapshot.value as? Bool {
                if opposingPlayer != false {
                    //play the game
                    _ = Timer.scheduledTimer(timeInterval: 2.0, target: self, selector: #selector(ViewController.timedLocationUpdate), userInfo: nil, repeats: true)
                }
                else {
                    //time to show the game ended because the other player left
                    self.playerLeftTextView.layer.isHidden = false
                }
            }
        })
    }
    
    func timedLocationUpdate() {
        //timer
        if playerNum == "player1" {
            let gameRef: FIRDatabaseReference = ref.child("games").child(finalCode!)
            gameRef.child("time").setValue(gameTimer)
            gameTimer = gameTimer + 1
            if gameTimer <= 20 {
                runTextView.layer.isHidden = false
                runTimerLabel.layer.isHidden = false
                var inverseRunTimer = 20
                inverseRunTimer = inverseRunTimer - gameTimer
                let inverseRunTimerText = String(inverseRunTimer)
                runTimerLabel.text = inverseRunTimerText
            }
            else {
                runTextView.layer.isHidden = true
                roleLabel.layer.isHidden = false
                runTimerLabel.layer.isHidden = true
                mainTimerLabel.layer.isHidden = false
                var inverseMainTimer = 220
                inverseMainTimer = inverseMainTimer - gameTimer
                let inverseMainTimerText = String(inverseMainTimer)
                mainTimerLabel.text = inverseMainTimerText
                gameView.layer.backgroundColor = UIColor.clear.cgColor
                if gameTimer >= 220 {
                    gameRef.child("winner").setValue("prey")
                }
                
                _ = gameRef.child("long").observeSingleEvent(of: .value, with: { (snapshot) in
                    if let longitude = snapshot.value as? Double {
                        _ = gameRef.child("lat").observeSingleEvent(of: .value, with: { (snapshot2) in
                            if let latitude = snapshot2.value as? Double {
                                let myLongitude = self.locationManager.location?.coordinate.longitude
                                let myLatitude = self.locationManager.location?.coordinate.latitude
                                let distance = self.getDistanceBetween(lat1: latitude, lon1: longitude, lat2: myLatitude!, lon2: myLongitude!);
                                gameRef.child("distance").setValue(distance)
                                //self.debugDistanceLabel.text = String(distance)
                                if Int(self.finalCode!)! % 2 == 1 {
                                    //prey
                                    if(distance < 14) {
                                        self.preyDistanceLabel.text = "***"
                                    }
                                    else if(distance < 20) {
                                        self.preyDistanceLabel.text = "**"
                                    }
                                    else {
                                        self.preyDistanceLabel.text = "*"
                                    }
                                }
                                else {
                                    //predator
                                    if(distance < 14) {
                                        self.hackingTimer = self.hackingTimer + 1
                                        if(self.hackingTimer >= 10) {
                                            gameRef.child("winner").setValue("predator")
                                        }
                                    }
                                    else {
                                        self.hackingTimer = 0;
                                    }
                                }
                            }
                        })
                    }
                })
                
                _ = gameRef.child("winner").observe(FIRDataEventType.value, with: { (snapshot) in
                    if let winner = snapshot.value as? String {
                        if winner == "prey" {
                            
                        }
                        else if winner == "predator" {
                            
                        }
                    }
                })
            }
        }
        else {
            let gameRef: FIRDatabaseReference = ref.child("games").child(finalCode!)
            _ = gameRef.child("time").observe(FIRDataEventType.value, with: { (snapshot) in
                if let timer = snapshot.value as? Int {
                    if timer <= 20 {
                        self.runTextView.layer.isHidden = false
                        self.runTimerLabel.layer.isHidden = false
                        var inverseRunTimer = 20
                        inverseRunTimer = inverseRunTimer - timer
                        let inverseRunTimerText = String(inverseRunTimer)
                        self.runTimerLabel.text = inverseRunTimerText
                    }
                    else {
                        self.runTextView.layer.isHidden = true
                        self.roleLabel.layer.isHidden = false
                        self.runTimerLabel.layer.isHidden = true
                        self.mainTimerLabel.layer.isHidden = false
                        var inverseMainTimer = 200
                        inverseMainTimer = inverseMainTimer - timer
                        let inverseMainTimerText = String(inverseMainTimer)
                        self.mainTimerLabel.text = inverseMainTimerText
                        self.gameView.layer.backgroundColor = UIColor.clear.cgColor
                        gameRef.child("long").setValue(self.locationManager.location?.coordinate.longitude)
                        gameRef.child("lat").setValue(self.locationManager.location?.coordinate.latitude)
                    }
                }
            })
        }
    }
    
    func getDistanceBetween(lat1: Double, lon1: Double, lat2: Double, lon2: Double) -> (Double) {
        let dis = sqrt(pow(lon1 - lon2, 2) + pow(lat1 - lat2, 2));
        return dis * 100000;
    }
    
    //fix these
    func getDistanceFromLatLonInM(lat1: Double, lon1: Double, lat2: Double, lon2: Double) -> (Double) {
        let R: Double = 6371; // Radius of the earth in km
        let dLat = deg2rad(deg: lat2-lat1);  // deg2rad below
        let dLon = deg2rad(deg: lon2-lon1);
        let a = sin(dLat/2) * sin(dLat/2) + cos(deg2rad(deg: lat1)) * cos(deg2rad(deg: lat2)) * sin(dLon/2) * sin(dLon/2)
        let c = 2 * atan2(sqrt(a), sqrt(1-a));
        let d = R * c * 1000; // Distance in m
        return d;
    }
    
    func deg2rad(deg: Double) -> (Double) {
        return deg * (M_PI/180)
    }
    
    func showBoundsAlert() {
        let alert = UIAlertController(title: "Wrong length", message: "Codes are three characters long.", preferredStyle: .alert);
        let action = UIAlertAction(title: "Got it", style: .default, handler: nil);
        alert.addAction(action);
        self.present(alert, animated: true, completion: nil);
    }
    
    func showIncorrectAlert() {
        let alert = UIAlertController(title: "Doesn't Exist", message: "No games exist with the code you enterred.", preferredStyle: .alert);
        let action = UIAlertAction(title: "Got it", style: .default, handler: nil);
        alert.addAction(action);
        self.present(alert, animated: true, completion: nil);
    }
    
    func randomString(length: Int) -> String {
        
        let letters : NSString = "0123456789"
        let len = UInt32(letters.length)
        
        var randomString = ""
        
        for _ in 0 ..< length {
            let rand = arc4random_uniform(len)
            var nextChar = letters.character(at: Int(rand))
            randomString += NSString(characters: &nextChar, length: 1) as String
        }
        
        return randomString
    }
    
    func locationManager(manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let locValue:CLLocationCoordinate2D = manager.location!.coordinate
        print("locations = \(locValue.latitude) \(locValue.longitude)")
    }
}

