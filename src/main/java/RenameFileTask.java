import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RenameFileTask extends Task {

    private String location;
    private List filesets = new ArrayList();
    private String jobId;
    private String postfix;

    public void setLocation(String location) {
        this.location = location;
    }

    public void addFileset(FileSet fileset) {
        filesets.add(fileset);
    }

    public void setJobId(String jobId){
        this.jobId=jobId;
    }

    private void validate() {
        if (location==null) throw new BuildException("location not set");
        if (filesets.size()<1) throw new BuildException("fileset not set");
        if (jobId.isEmpty()){
            LocalTime localTime = LocalTime.now(ZoneId.of("Europe/Minsk"));
            postfix=localTime.format(DateTimeFormatter.ofPattern("H-mm-ss"));
        }else{
            postfix=jobId;
        }
    }

    public void execute() throws BuildException{
        validate();
        for (Object fileset : filesets) {
            FileSet fs = (FileSet) fileset;
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            String[] includedFiles = ds.getIncludedFiles();
            for (String includedFile : includedFiles) {
                File current=new File(location + "/" + includedFile);
                String[] split=current.getName().split("\\.(?=[^\\.]+$)");
                String name = split[0]+"-"+postfix+"."+split[1];
                if(!current.renameTo(new File(current.getParent()+"/"+name+"."+split[1])))
                   log("error rename");
            }
        }
    }
}
