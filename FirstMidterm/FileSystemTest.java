package PrvKolokvium;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

interface IFile extends Comparable<IFile>
{
    String getFileName();
    long getFileSize();
    String getFileInfo(String indent);
    void sortBySize();
    long findLargestFile();

    @Override
    default int compareTo(IFile o) {
        return Long.compare(this.getFileSize(),o.getFileSize());
    }
}
class File implements IFile
{
    String name;
    long size;

    public File(String name, long size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public long getFileSize() {
        return size;
    }

    @Override
    public String getFileInfo(String indent) {
        return indent + String.format("File name: %10s File size: %10d\n",name,size);
    }

    @Override
    public void sortBySize() {
    }

    @Override
    public long findLargestFile() {
        return size;
    }
}
class Folder extends File
{
    List<IFile> files;

    public Folder(String name) {
        super(name, 0);
        this.files = new ArrayList<>();
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public long getFileSize() {
        return files.stream().mapToLong(IFile::getFileSize).sum();
    }

    @Override
    public String getFileInfo(String indent) {
        String result =  indent + String.format("Folder name: %10s Folder size: %10d\n",name,getFileSize());
        for (IFile iFile: files)
        {
            result += iFile.getFileInfo(indent + "\t");
        }
        return result;
    }

    @Override
    public void sortBySize() {
        Collections.sort(files);
        for (IFile iFile: files)
        {
            iFile.sortBySize();
        }
    }

    @Override
    public long findLargestFile() {
        return files.stream().mapToLong(IFile::findLargestFile).max().orElse(0);
    }

    public void addFile(IFile file) throws FileNameExistsException {
        for (IFile iFile: files)
        {
            if (iFile.getFileName().equals(file.getFileName()))
                throw new FileNameExistsException(iFile.getFileName(),name);
        }
        files.add(file);
    }
}

class FileSystem
{
    Folder root;

    public FileSystem()
    {
        this.root = new Folder("root");
    }

    public void addFile(IFile file) throws FileNameExistsException {
        root.addFile(file);
    }

    public void sortBySize() {
        root.sortBySize();
    }

    public long findLargestFile() {
        return root.findLargestFile();
    }

    @Override
    public String toString() {
        return root.getFileInfo("");
    }
}

class FileNameExistsException extends Exception
{
    public FileNameExistsException(String fileName, String name)
    {
        super("There is already a file named " + fileName + " in the folder " + name);
    }
}

public class FileSystemTest {

    public static Folder readFolder (Scanner sc)  {

        Folder folder = new Folder(sc.nextLine());
        int totalFiles = Integer.parseInt(sc.nextLine());

        for (int i=0;i<totalFiles;i++) {
            String line = sc.nextLine();

            if (line.startsWith("0")) {
                String fileInfo = sc.nextLine();
                String [] parts = fileInfo.split("\\s+");
                try {
                    folder.addFile(new File(parts[0], Long.parseLong(parts[1])));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
            else {
                try {
                    folder.addFile(readFolder(sc));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return folder;
    }

    public static void main(String[] args)  {

        //file reading from input

        Scanner sc = new Scanner (System.in);

        System.out.println("===READING FILES FROM INPUT===");
        FileSystem fileSystem = new FileSystem();
        try {
            fileSystem.addFile(readFolder(sc));
        } catch (FileNameExistsException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("===PRINTING FILE SYSTEM INFO===");
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING FILE SYSTEM INFO AFTER SORTING===");
        fileSystem.sortBySize();
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING THE SIZE OF THE LARGEST FILE IN THE FILE SYSTEM===");
        System.out.println(fileSystem.findLargestFile());

    }
}