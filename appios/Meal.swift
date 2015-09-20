//
//  Meal.swift
//  ahead
//
//  Created by Syed Haider on 9/19/15.
//  Copyright © 2015 syed. All rights reserved.
//

import Foundation

import UIKit

class Meal {
    var name: String
    var photo: UIImage?
    var orderbtn: UIButton
    var notifybtn: UIButton
    
    init?(name: String, photo: UIImage?, orderbtn: UIButton, notifybtn: UIButton) {
        self.name = name
        self.photo = photo
        self.orderbtn = orderbtn
        self.notifybtn = notifybtn
        
        if name.isEmpty  {
            return nil
        }
    }
    
}