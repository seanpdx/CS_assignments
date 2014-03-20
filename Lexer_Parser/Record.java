// to be used with Lab 3 - Record for symbol table.
import java.util.*;
public class Record {
// Doubles as Symbol Table and Memory. No Reason why not.
    private String Name;
    private String Type;
    private String  Kind;
    private int StartAddress;
    private int EndAddress;
    private String Contents;
    private Vector expression;
    private String[] arrayContents1;
    private String[][] arrayContents2;
    private boolean isArray;


public Record (String rName, String rType, String rKind, int sAddress, int eAddress, String rContents)
    {
      //populate the fields
      Name = rName;
      Type = rType;
      Kind = rKind;
      StartAddress = sAddress;
      EndAddress = eAddress;
      Contents = rContents;
      
    }
public Record()
{
      Name = "";
      Type = "";
      Kind = "";
      StartAddress = 0;
      EndAddress = 0;
      Contents = "";
      expression = new Vector<String>();
      isArray = false;
      
}
public void arraySetup(int dim1, int dim2){
    isArray = true;
    if (dim2 == -1){
        arrayContents1 = new String[dim1];
        EndAddress = StartAddress + dim1;


    }else{
        // two dim.
        arrayContents2 = new String[dim1][dim2];
        EndAddress = StartAddress + dim1*dim2;
    }
}
public String returnArrayContents(int dim1, int dim2 ){
    // one dimension return
    if (dim2 == -1){

        return (String) arrayContents1[dim1];
    }else{
        // two dimension return
      return (String) arrayContents2[dim1][dim2];
    }
}
  public void ArrayStoreVal (String value, int dim1, int dim2){
       if (dim2 == -1){

       arrayContents1[dim1] = value;
    }else{
        // two dimension return
      arrayContents2[dim1][dim2] = value;
    }
  }

public void StoreExpr(String expr){
    expression.add(expr);
    return;
}
public void PrintExpr(){
      while(!expression.isEmpty()){
                    System.out.println(expression.firstElement());
                    expression.remove(0);
                }
    return;
}

public void ChangeContents ( String changeto, String[] temp)
{
   if (!isArray){
  Contents = changeto;
   }else{
       int dim1 = Integer.parseInt(temp[8]);
       int dim2 = Integer.parseInt(temp[9]);
       // check if one or two dimensions
       // one
       if (dim2 == -1){
          arrayContents1[dim1] = changeto;
       }else{
           // two dim
           arrayContents2[dim1][dim2] = changeto;
       }


   }
}
public void ChangeName ( String changeto)
{
  Name = changeto;
}
public void ChangeType ( String changeto)
{
  Type = changeto;
}
public void ChangeKind ( String changeto)
{
  Kind = changeto;
}
public String GetType ( )
{
  return Type;
}

public void SetAddress ( int Sadd, int Eadd)
{
  StartAddress = Sadd;
  EndAddress = Eadd;
}
public int getEndAddress ()
{
return EndAddress;
}

public String getName(){
    return Name;
}
public String getContents(String[] temp){
    if(!isArray){

    return Contents;
    }else{
         int dim1 = Integer.parseInt(temp[8]);
       int dim2 = Integer.parseInt(temp[9]);
        if (dim2 == -1){
         return arrayContents1[dim1];
       }else{
           // two dim
           return arrayContents2[dim1][dim2];
       }

    }
}
public String print ()
{
return Name + "\t"+ Type + "\t" + Kind + "\t" + StartAddress;
}

public String printMemory (){
    return StartAddress + "-" + EndAddress + "\t" + Contents;
}
}
