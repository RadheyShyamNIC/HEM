package in.nic.hem.mypkg;

public class DropDownItem {
    String itemText;
    int itemID;
    public DropDownItem(String id,String txt){
        itemID = Integer.parseInt(id);
        itemText = txt;
    }
    @Override
    public String toString() {
        return itemText;
    }

    public int getItemID() {
        return itemID;
    }

    public String getItemText() {
        return itemText;
    }
}
