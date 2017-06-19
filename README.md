# FileBase
Directory management for easy application,include some I/O tools,such as File handles,Assets handles,Storage handles,Zip handles.
# Dependency
```gradle
dependencies {
  compile 'com.kk.taurus.filebase:FileBase:1.1.4'
}
```
# Use Sample
### Directory management
```java
public class TestFileBase extends FileBase {

    public static final String APP_MANAGE_ROOT_DIR_NAME             = "TestFileBase";
    public static final String APP_MANAGE_IMAGE_CACHE_DIR_NAME      = "image/cache";
    public static final String APP_MANAGE_LOG_DIR_NAME              = "log";

    public TestFileBase(Context context) {
        super(context);
    }

    public File getImageCacheDir(){
        return createDir(APP_MANAGE_IMAGE_CACHE_DIR_NAME);
    }
    public File getLogDir(){
        return createDir(APP_MANAGE_LOG_DIR_NAME);
    }
    @Override
    public String getManageRootDirName() {
        return APP_MANAGE_ROOT_DIR_NAME;
    }
    @Override
    public int getRootParentDirType() {
        return MANAGE_PARENT_DIR_APP_EXTERNAL_ROOT;
    }
    @Override
    public int getRootParentSpareDirType() {
        return MANAGE_PARENT_DIR_APP_EXTERNAL_CACHE_FILES;
    }
}
```
#### use directory management
```java
//use it
TestFileBase testFileBase = new TestFileBase(this);
File cacheDir = testFileBase.getImageCacheDir();
File logDir = testFileBase.getLogDir();
```
### Get Storage devices
```java
List<Storage> storages = StorageEngine.getStorageList(context);
for(Storage storage : storages){
    //....
}
```
### Assets Handle
```java
String content = AssetsEngine.getAssetsStringContent(this,"test.txt");
System.out.println("assets_content  : " + content);

String content1 = AssetsEngine.getAssetsStringContent(this,new String[]{"dir1"},"test1.txt");
System.out.println("assets_content1  : " + content1);

String content2 = AssetsEngine.getAssetsStringContent(this,new String[]{"dir1","dir2"},"test2.txt");
System.out.println("assets_content2  : " + content2);

AssetsEngine.copyAssetsToDir(this,new String[]{"dir1","dir2"},"test2.txt",testFileBase.getAssetsDir());

AssetsEngine.copyAssetsToDir(this,"ikanweb.zip",testFileBase.getAssetsDir());
```