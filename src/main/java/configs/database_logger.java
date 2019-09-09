package configs;
 
import org.springframework.batch.item.ItemProcessor;
import com.etgtest.data.model.test_data;


 
public class database_logger implements ItemProcessor<test_data, test_data>
{
    public test_data process(test_data test_data) throws Exception
    {
        System.out.println("Inserting data : " + test_data);
        return test_data;
    }
}