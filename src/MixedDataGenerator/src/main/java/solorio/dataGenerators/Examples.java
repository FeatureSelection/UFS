package solorio.dataGenerators;

import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Instances;

public class Examples 
{

    //Example 1: Default generator
	public void example1(){ 
        try {
            MixedDataGenerator mixedDataGenerator = new MixedDataGenerator();

            mixedDataGenerator.defineDataFormat();
            Instances instances = mixedDataGenerator.generateExamples();
            System.out.println(instances.toString());
        } catch (Exception e) {
            Logger.getLogger(Examples.class.getName()).log(Level.SEVERE, null, e);
            System.out.println(e);
        } 

    } 
    //Example 2: Setting custom custom values
    public void example2(){
        try {
            MixedDataGenerator mixedDataGenerator = new MixedDataGenerator();
            //General settings
            mixedDataGenerator.setRelationName("Synthetic Mixed Dataset Example2"); //The name of the synthetic dataset
            mixedDataGenerator.setNumClasses(3); // The number of classes in the dataset
            mixedDataGenerator.setClassBalanceRate(0.8); // Parameter for controlling the balance rate of objects in each class. Any value in interval (0,1] can be set.
            mixedDataGenerator.setNumExamples(500); // the number of objects to generate
            mixedDataGenerator.setSeed(1); //The seed used for generating the dataset

            //Feature settings

            //Numerical feature
            mixedDataGenerator.setNumOfNumericRelevantAttributes(20);  //The number of relevant features to generate
            mixedDataGenerator.setNumOfNumericIrrelevantAttributes(10); //The number of irrelevat features to generate
            mixedDataGenerator.setNumOfNumericRedundantAttributes(5); //The number of redundant features to generate
            mixedDataGenerator.setEqualIntervalForIrrelevantNumericAttributes(false); // A boolean indicator for setting the 

            //Non-numerical features
            mixedDataGenerator.setNumOfNominalRelevantAttributes(10); 
            mixedDataGenerator.setNumOfNominalIrrelevantAttributes(15);
            mixedDataGenerator.setNumOfNominalRedundantAttributes(10);
            mixedDataGenerator.setMaxNumOfValuesForNominalAttributes(6); //The maximun number of allowed values for non-numerical features.
 
            mixedDataGenerator.defineDataFormat();
            Instances generatedData = mixedDataGenerator.generateExamples();

            System.out.println(generatedData.toString());
            
        } catch (Exception e) {
            Logger.getLogger(Examples.class.getName()).log(Level.SEVERE, null, e);
            System.out.println(e);
        }

    }

    public static void main(String[] args) {
        Examples examples = new Examples();
        examples.example2();
    }

    
}
