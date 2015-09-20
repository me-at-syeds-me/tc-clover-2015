//
//  MealTableViewController.swift
//  ahead
//
//  Created by Syed Haider on 9/19/15.
//  Copyright © 2015 syed. All rights reserved.
//

import UIKit

class MealTableViewController: UITableViewController {

    // MARK: Properties
    
    var meals = [Meal]()
    
    @IBAction func postToServer(sender: AnyObject) {
        let button = sender as! UIButton
        let view = button.superview!
        let cell = view.superview as! MealTableViewCell
        
        let indexPath = tableView.indexPathForCell(cell)
        let item = indexPath?.item;
        postToServerFunction(item)

    }
    func postToServerFunction(item: Int?) {
        print("Button Pressed")
        let url: NSURL = NSURL(string: "http://super.bestpayapp.com/ahead/order.php")!
        let request:NSMutableURLRequest = NSMutableURLRequest(URL:url)
        var bodyData = "event=0&order=1010&address=1233 17th Street, San Francisco, CA 94107"
        if item == 0 {
            bodyData = "event=0&order=1010&address=1233 17th Street, San Francisco, CA 94107"
        }
        if item == 1 {
            bodyData = "event=0&order=1020&address=1233 17th Street, San Francisco, CA 94107"
        }
        if item == 2 {
            bodyData = "event=0&order=1030&address=1233 17th Street, San Francisco, CA 94107"
        }
        
        request.HTTPMethod = "POST"
        
        request.HTTPBody = bodyData.dataUsingEncoding(NSUTF8StringEncoding);
        NSURLConnection.sendAsynchronousRequest(request, queue: NSOperationQueue.mainQueue())
            {
                (response, data, error) in
                print(response)
                
        }
    }
    
    @IBAction func postToServerNotify(sender: AnyObject) {
        let button = sender as! UIButton
        let view = button.superview!
        let cell = view.superview as! MealTableViewCell
        
        let indexPath = tableView.indexPathForCell(cell)
        let item = indexPath?.item;
        
        postToNotifyFunction(item)

    }
    
    func postToNotifyFunction(item: Int?) {
        
        var url: NSURL = NSURL(string: "http://localhost:8888/ahead/headingout.php")!
        var request:NSMutableURLRequest = NSMutableURLRequest(URL:url)
        var bodyData = "event=1&order=1010&address=1233 17th Street, San Francisco, CA 94107"
        if item == 0 {
            bodyData = "event=1&order=1010&address=1233 17th Street, San Francisco, CA 94107"
        }
        if item == 1 {
            bodyData = "event=1&order=1020&address=1233 17th Street, San Francisco, CA 94107"
        }
        if item == 2 {
            bodyData = "event=1&order=1030&address=1233 17th Street, San Francisco, CA 94107"
        }
        request.HTTPMethod = "POST"
        
        request.HTTPBody = bodyData.dataUsingEncoding(NSUTF8StringEncoding);
        NSURLConnection.sendAsynchronousRequest(request, queue: NSOperationQueue.mainQueue())
            {
                (response, data, error) in
                print(response)
                
        }
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Load the sample data.
        loadSampleMeals()
    }

    func loadSampleMeals(){
        let photo1 = UIImage(named: "meal1.jpg")!
        let orderbtn1 = UIButton()
        let notifybtn1 = UIButton()
        let meal1 = Meal (name: "Steak Sandwich", photo:photo1, orderbtn:orderbtn1, notifybtn:notifybtn1)!
        
        let orderbtn2 = UIButton()
        let notifybtn2 = UIButton()
        let photo2 = UIImage(named: "meal2.png")!
        let meal2 = Meal(name: "Chicken Panini", photo: photo2, orderbtn:orderbtn2, notifybtn:notifybtn2)!
        
        let orderbtn3 = UIButton()
        let notifybtn3 = UIButton()
        let photo3 = UIImage(named: "meal3.jpg")!
        let meal3 = Meal(name: "Chicken Tikka Boti", photo: photo3, orderbtn:orderbtn3, notifybtn:notifybtn3)!
        
        meals += [meal1, meal2, meal3]
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return meals.count
    }

    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        // Table view cells are reused and should be dequeued using a cell identifier.
        let cellIdentifier = "MealTableViewCell"
        let cell = tableView.dequeueReusableCellWithIdentifier(cellIdentifier, forIndexPath: indexPath) as! MealTableViewCell
        
        // Fetches the appropriate meal for the data source layout.
        let meal = meals[indexPath.row]
        
        cell.nameLabel.text = meal.name
        cell.photoImageView.image = meal.photo

        return cell

    }
    

    /*
    // Override to support conditional editing of the table view.
    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        if editingStyle == .Delete {
            // Delete the row from the data source
            tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
        } else if editingStyle == .Insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(tableView: UITableView, moveRowAtIndexPath fromIndexPath: NSIndexPath, toIndexPath: NSIndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(tableView: UITableView, canMoveRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
