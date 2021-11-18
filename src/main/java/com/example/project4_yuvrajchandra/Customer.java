import java.util.ArrayList;

/**
 * Class Customer
 */
public abstract class Customer {

  //
  // Fields
  //
  private ArrayList<ShippingAddress> Addresses;
  private String Name;
  private int customerID;
  private static int nextID = 5000; //all preloaded customers from the text file must have IDs lower than 5000
  
  //
  // Constructors
  //
  public Customer (String Name, int ID) {
    this.Name = Name;
    customerID = ID;
    Addresses = new ArrayList<ShippingAddress>();
  };

  /**
   * @param        custName
   */
  public Customer(String custName)
  {
    Name = custName;
    nextID++;
    customerID = nextID;
    Addresses = new ArrayList<ShippingAddress>();
  }

  //
  // Methods
  //

public abstract double payForOrder(ArrayList<MerchandiseItem> itemsInOrder);

  public double payOutstandingBalance(){
    return 0.0;
  }

  public void arrangeDelivery(){
    System.out.println("Dilivery for "+ Name + " can be delivered at any time");
  }

  /**
   * Get the value of Addresses
   * @return the value of Addresses
   */
  public ArrayList<ShippingAddress> getAddresses () {
    //this returns a copy of the list so that the caller cannot change the private instance variable
    return new ArrayList<ShippingAddress>(Addresses);
  }

  /**
   * Get the value of Name
   * @return the value of Name
   */
  public String getName () {
    return Name;
  }

  /**
   * Get the value of customerID
   * @return the value of customerID
   */
  public int getCustomerID () {
    return customerID;
  }

  //
  // Other methods
  //


  /**
   * @param        newAddress
   */
  public void addAddress(ShippingAddress newAddress)
  {
    Addresses.add(newAddress);
  }



  /**
   * @return       String
   */
  @Override
  public String toString()
  {
    return "Customer Name: " + Name +"\nCustomerID: "+customerID + "\nWith "+Addresses.size() + " addresses on file";
  }


}
