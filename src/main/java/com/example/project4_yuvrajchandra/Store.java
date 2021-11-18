import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Class Store
 */
public class Store {

  //
  // Fields
  //

  private ArrayList<Order> Orders;
  private ArrayList<Customer> Customers;
  private ArrayList<MerchandiseItem> Stock;
  private double revenue;
  
  //
  // Constructors
  //
  public Store () {
    Orders = new ArrayList<Order>();
    Customers = new ArrayList<Customer>();
    Stock = new ArrayList<MerchandiseItem>();
    revenue = 0.0;
  }
  
  //
  // Methods
  //


  public ArrayList<MerchandiseItem> getStock(){
    return Stock;
  }


  public static void main(String[] args) throws IOException
  {
    var comp152Inc = new Store();
    comp152Inc.runStore();

  }

  /**
   */
  public void runStore() throws IOException
  {
    var inputReader = new Scanner(System.in);
    loadStartingCustomers(inputReader);
    loadStockItems();
    while(true){ //the main run loop
      printMainMenu();
      var userChoice = inputReader.nextInt();
      switch (userChoice){
        case 1:
          addCustomer(inputReader);
          break;
        case 2:
          var selectedCustomer =selectCustomer(inputReader);
          if(selectedCustomer.isPresent())
            manageCustomer(selectedCustomer.get());
          break;
        case 3:
          System.exit(0);
        case 4:
          CollectOutstandingBalancesFromPurchaseOrders();
          break;
        case 5:
          System.out.println("Our Company has collected $"+revenue + " in revenue so far");
          break;
        default:
          System.out.println("\n%%%%%%Invalid selection, please choose one of the options from the menu%%%%%%\n");
      }
    }
  }

  private void CollectOutstandingBalancesFromPurchaseOrders() {
    for(var customer: Customers){
      //loop through all the customers and ask each one to pay their balance, everyone but business customers will return 0
      //then add those new payments to revenue
      var recentPayment = customer.payOutstandingBalance();
      revenue+= recentPayment;
    }
  }

  private void loadStockItems() {
    List<String> allLines = null;

    try {
      Path itemsFilePath = Paths.get("ItemsForSale.txt");
      allLines = Files.readAllLines(itemsFilePath);
    }
    catch (IOException e){
        System.out.println("Faled to read the items for sale file, Shutting down for now");
        System.exit(-1);
    }
    //if we got here we should have a real value in allLines
    for(var entry: allLines){
      var entryValues = entry.split(",");
      //I've used a simple number to determine what type of merchandise this is
      //1 is food, 2 is clothing, and three is general merchandise
      ItemType thisItemsType = ItemType.Clothing;
      switch (entryValues[2]){
        case "1"->  thisItemsType = ItemType.WICFood;
        case "2" -> thisItemsType= ItemType.Clothing; //this one is actually redundant, but included for completeness
        case "3" -> thisItemsType = ItemType.GeneralMerchadise;
      }
      var price = Double.parseDouble(entryValues[1]);
      var newItem = new MerchandiseItem(entryValues[0], price, thisItemsType);
      Stock.add(newItem);
    }
  }

  private static void printMainMenu() {
    System.out.println("*****************************************************************************");
    System.out.println("Welcome to the the 1980s Comp152 Store interface, what would you like to do?");
    System.out.println("   [1] Add Customer");
    System.out.println("   [2] Select Customer");
    System.out.println("   [3] Exit the program");
    System.out.println("   [4] Collect outstanding Balances from Purchase Orders");
    System.out.println("   [5] Show total revenue"); //this one wasn't required by the assignment, I wanted to see to make it easier to make sure things were working
    System.out.println("*****************************************************************************");
    System.out.print("Enter the number of your choice:");
  }




  private void loadStartingCustomers(Scanner inputReader) throws IOException {
    Path fullPathName;
    String filename;
    while(true) { //this is for some error checking. It was not required by the assignment
      System.out.print("Enter the name of the file to load customers:");
      filename = inputReader.nextLine();
      fullPathName = Paths.get(filename);
      if (!Files.exists(fullPathName)){ //these three lines checks to see if the file exists, if not go
        System.out.println("No file with that name, please try again....");//do the loop again
      }
      else
        break;
    }
    //if we got here the file must be real
    var allLines = Files.readAllLines(fullPathName);
    // now create customers for all of the lines in the file
    Customer currentCustomer = null;
    for(var line: allLines){
      var splitLine = line.split(",");
      switch(splitLine[2]){//I've added a new third item that tells the customer type, R: Residential, B:business, or T:Taxexempt
        case "R"->{
          currentCustomer = new ResidentialCustomer(splitLine[0], Integer.parseInt(splitLine[1]));
        }
        case "B"->{
          currentCustomer = new BusinessCustomer(splitLine[0], Integer.parseInt(splitLine[1]));
        }
        case "T"->{
          currentCustomer = new TaxExemptCustomer(splitLine[0], Integer.parseInt(splitLine[1]));
        }
        default -> { //this was not needed, but I wanted to model throwing an exception.
          throw new IOException("Bad file format - invalid customer type specified");
        }
      }

      Customers.add(currentCustomer);
    }
  }


  /**
   * @param        address
   * @param        cust
   */
  public void makeOrder(ShippingAddress address, Customer cust, Scanner commandLineInput)
  {
    var cart = new ArrayList<MerchandiseItem>();
    System.out.println("Preparing to make order........");
    while(true){
      printStock();
      System.out.print("type the item number for your order. Select a negative number to end.");
      var choice = commandLineInput.nextInt();
      if (choice<0)
        break;
      if(choice>=Stock.size())//error checking
        continue;
      //if we got here we had a good selection.
      cart.add(Stock.get(choice));//add the item to the cart
    }
    //after adding all the items to the cart,
    var newOrder = new Order(address,cust,cart);
    Orders.add(newOrder);
    System.out.println(".......New order successfully created");
    revenue +=cust.payForOrder(cart);
    cust.arrangeDelivery();

  }

  private void printStock() {
    var count = 0;
    for(var itemForSale: Stock){
      System.out.println("["+count+"] "+itemForSale.getName()+" cost $"+itemForSale.getPrice());
      count++;
    }
  }


  /**
   */
  public void addCustomer(Scanner inputReader)
  {
    //because we just came from a nextInt, there is an orphaned \n on the input stream eat it
    inputReader.nextLine();
    System.out.println("Adding Customer........");
    System.out.print("Enter the new Customers name:");
    var newName = inputReader.nextLine();
    System.out.println("What kind of customer is this? \n[1]Residential\n[2]Business,[3]Tax-exempt");
    var typeNum = inputReader.nextInt();
    switch (typeNum){
      case 1-> {
        var newCustomer = new ResidentialCustomer(newName);
        Customers.add(newCustomer);
      }
      case 2-> {
        var newCustomer = new BusinessCustomer(newName);
        Customers.add(newCustomer);
      }
      case 3->{
        var newCustomer = new TaxExemptCustomer(newName);
        Customers.add(newCustomer);
      }
    }

    System.out.println(".....Finished adding new Customer Record");
  }


  /**
   * @return       Customer
   * the original UML called for returning a Customer Object rather than an Optional
   * since I didn't know when I designed this if we would hit optional by then or not
   * but I invited anyone who asked to use Optional if they wanted to
   * either way is perfectly fine
   */
  public Optional<Customer> selectCustomer(Scanner reader)
  {
    System.out.print("Enter the ID of the customer to select:");
    var enteredID = reader.nextInt();
    for(var currentCustomer: Customers){
      if(currentCustomer.getCustomerID()==enteredID)
        return Optional.of(currentCustomer);
    }
    //If we looked through the whole list and didn't find that customer tell the user
    System.out.println("==========================> No customer with customer ID:"+enteredID);
    return Optional.empty();
  }


  /**
   * @param        selectedCustomer
   */
  public void manageCustomer(Customer selectedCustomer)
  {
    //many of you passed our existing scanner in as an extra parameter, that is more than fine.
    //I'm doing it this way to create a new one, both work the same way since both are reading from
    //System.in
    Scanner secondScanner = new Scanner(System.in);
    while(true){ //the menu loop for the manage Customer menu
        printCustomerMenu(selectedCustomer.getName());
        var userChoice = secondScanner.nextInt();
      //the syntax below is part of the 'enhanced switch' available in very recent versions of java
      //I used the traditional version in the previous switch above, so I'm experimenting here
      //this 'enhanced switch' doesn't require break statements.
        switch (userChoice){
          case 1 ->addAddress(secondScanner, selectedCustomer);
          case 2->{
            ShippingAddress selectedAddress = pickAddress(secondScanner,selectedCustomer);
            makeOrder(selectedAddress,selectedCustomer, secondScanner);
          }
          case 3-> {return;}
          default->System.out.println("Invalid option selected");
        }
    }
  }

  private ShippingAddress pickAddress(Scanner secondScanner, Customer selectedCustomer) {
    var customerAddresses = selectedCustomer.getAddresses();
    if (customerAddresses.size() ==0){ //note, this error checking was not required
      System.out.println("This customer has no addresses on file, please add an address");
      addAddress(secondScanner,selectedCustomer);
      return selectedCustomer.getAddresses().get(0); //if we are here there is only one address so use it
    }
    var count = 0;
    //if we are here then there was at least one address already in the customer.
    System.out.println("Please select a shipping address from those the customer has on file");
    for (var address : customerAddresses) {   //I'm being a little 'cute'/clever here
      System.out.print("[" + count + "]"); //but you could do for(int count; count < customerAddresses.size(); count++ for the same effect
      System.out.println(address.toString());
      count++;
    }
    System.out.print("Enter the number of the address for this order:");
    var addressNum = secondScanner.nextInt();
    //again more error checking that I didn't require of you here below:
    if (addressNum >= customerAddresses.size()){
      System.out.println("Invalid entry, defaulting to the first address on file...");
      return customerAddresses.get(0);
    }
    else
      return customerAddresses.get(addressNum);//I asked the user for the number representing the addresses position in the arrayList
  }

  private void addAddress(Scanner secondScanner, Customer selectedCustomer) {
    System.out.println("Adding new address for "+ selectedCustomer.getName());
    secondScanner.nextLine(); //lets eat the leftover '\n' from previous nextInt
    System.out.print("Enter Address Line 1:");
    var line1 = secondScanner.nextLine();
    System.out.print("Enter Address Line 2 or <enter> if there is none:");
    var line2 = secondScanner.nextLine();
    System.out.print("Enter the address City:");
    var city = secondScanner.nextLine();
    System.out.print("Enter address state:");
    var state = secondScanner.nextLine();
    System.out.print("Enter the postal code:");
    var postCode = secondScanner.nextLine();
    var newAddress  = new ShippingAddress(line1,line2,city,state,postCode);
    selectedCustomer.addAddress(newAddress);
  }

  private void printCustomerMenu(String custName) {
    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    System.out.println("What do you want to do for Customer " + custName+"?");
    System.out.println("   [1] Add Address to customer");
    System.out.println("   [2] Make an order for the customer");
    System.out.println("   [3] return to the main menu");
    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    System.out.print("Enter the number of your choice:");
  }


}
