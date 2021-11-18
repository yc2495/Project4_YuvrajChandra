import java.util.ArrayList;

/**
 * Class Order
 */
public class Order {

  //
  // Fields
  //

  private ShippingAddress destination;
  private Customer orderedBy;
  ArrayList<MerchandiseItem> cartForOrder;


  /**
   * @param        dest
   * @param        cust
   */
  public Order(ShippingAddress dest, Customer cust, ArrayList<MerchandiseItem> itemsOrdered)
  {
    destination = dest;
    orderedBy = cust;
    cartForOrder = itemsOrdered;
  }

  //
  // Methods
  //

  /**
   * @return       String
   */
  public String getDestination()
  {
    return destination.toString();
  }


  /**
   * @return       String
   */
  public String getOrderer()
  {
    return orderedBy.toString();
  }





}
