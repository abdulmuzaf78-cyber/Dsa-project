import java.util.*;

class DigitalFileSystem {

    static abstract class StorageItem {
        String name;
        int size;

        StorageItem(String name, int size) {
            this.name = name;
            this.size = size;
        }

        abstract void display();
    }

    static class MyFile extends StorageItem {
        int id;
        String type;
        ArrayList<Integer> blocks;

        MyFile(int id, String name, String type, int size) {
            super(name, size);
            this.id = id;
            this.type = type;
            blocks = new ArrayList<>();
        }

        public void display() {
            System.out.println(name + " | " + type + " | " + size + "MB");
        }
    }

    static class Folder extends StorageItem {
        LinkedList<MyFile> files;
        ArrayList<Folder> folders;

        Folder(String name) {
            super(name, 0);
            files = new LinkedList<>();
            folders = new ArrayList<>();
        }

        void addFile(MyFile file) {
            files.add(file);
        }

        void removeFile(String name) {
            files.removeIf(f -> f.name.equalsIgnoreCase(name));
        }

        public void display() {
            System.out.println("\nFolder: " + name);

            for (Folder f : folders)
                System.out.println("SubFolder: " + f.name);

            for (MyFile f : files)
                f.display();
        }
    }

    static class AllocationManager {
        PriorityQueue<Integer> freeBlocks;

        AllocationManager() {
            freeBlocks = new PriorityQueue<>();

            for (int i = 1; i <= 50; i++)
                freeBlocks.add(i);
        }

        ArrayList<Integer> allocate(int size) {
            ArrayList<Integer> allocated = new ArrayList<>();

            for (int i = 0; i < size && !freeBlocks.isEmpty(); i++)
                allocated.add(freeBlocks.poll());

            return allocated;
        }

        void release(ArrayList<Integer> blocks) {
            freeBlocks.addAll(blocks);
        }

        void showFree() {
            System.out.println("Available Blocks: " + freeBlocks.size());
        }
    }

    static class HistoryNode {
        String operation;
        HistoryNode prev;
        HistoryNode next;

        HistoryNode(String op) {
            operation = op;
        }
    }

    static class History {
        HistoryNode head;
        HistoryNode tail;

        void add(String op) {
            HistoryNode node = new HistoryNode(op);

            if (head == null) {
                head = tail = node;
            } else {
                tail.next = node;
                node.prev = tail;
                tail = node;
            }
        }

        void show() {
            HistoryNode temp = head;

            while (temp != null) {
                System.out.println(temp.operation);
                temp = temp.next;
            }
        }
    }

    static class FileSystem {

        Folder root = new Folder("Root");

        AllocationManager allocator =
                new AllocationManager();

        Stack<MyFile> deleted =
                new Stack<>();

        Queue<String> requests =
                new LinkedList<>();

        History history =
                new History();

        int fileID = 1;

        void createFile(
                String folder,
                String fileName,
                String type,
                int size
        ) {

            Folder f = getFolder(folder);

            if (f == null) {
                System.out.println("Folder Not Found");
                return;
            }

            MyFile file =
                    new MyFile(
                            fileID++,
                            fileName,
                            type,
                            size
                    );

            file.blocks =
                    allocator.allocate(size);

            f.addFile(file);

            requests.add("CREATE");

            history.add(
                    "Created "
                            + fileName
            );

            System.out.println(
                    "File Created"
            );

            System.out.println(
                    "Blocks: "
                            + file.blocks
            );
        }

        void deleteFile(
                String folder,
                String fileName
        ) {

            Folder f =
                    getFolder(folder);

            if (f == null)
                return;

            for (MyFile file : f.files) {

                if (file.name.equalsIgnoreCase(fileName)) {

                    deleted.push(file);

                    allocator.release(
                            file.blocks
                    );

                    f.removeFile(fileName);

                    requests.add("DELETE");

                    history.add(
                            "Deleted "
                                    + fileName
                    );

                    System.out.println(
                            "Deleted"
                    );

                    return;
                }
            }

            System.out.println(
                    "File Not Found"
            );
        }

        void restore() {

            if (deleted.empty()) {
                System.out.println(
                        "Nothing To Restore"
                );

                return;
            }

            MyFile file =
                    deleted.pop();

            file.blocks =
                    allocator.allocate(
                            file.size
                    );

            root.files.add(file);

            history.add(
                    "Restored "
                            + file.name
            );

            System.out.println(
                    "Restored"
            );
        }

        void search(
                String name
        ) {

            for (MyFile f :
                    root.files) {

                if (
                        f.name
                                .equalsIgnoreCase(
                                        name
                                )
                ) {

                    f.display();

                    return;
                }
            }

            System.out.println(
                    "Not Found"
            );
        }

        Folder getFolder(
                String name
        ) {

            if (
                    root.name
                            .equalsIgnoreCase(
                                    name
                            )
            )
                return root;

            for (
                    Folder f :
                    root.folders
            ) {

                if (
                        f.name
                                .equalsIgnoreCase(
                                        name
                                )
                )
                    return f;
            }

            return null;
        }

        void createFolder(
                String name
        ) {

            root.folders.add(
                    new Folder(name)
            );

            history.add(
                    "Folder Created "
                            + name
            );
        }

        void viewStorage() {

            root.display();

            allocator.showFree();
        }

        void showHistory() {
            history.show();
        }

    }

    public static void main(String[] args) {

        Scanner sc =
                new Scanner(System.in);

        FileSystem fs =
                new FileSystem();

        while (true) {

            System.out.println(
                    "\n1.Create Folder"
            );

            System.out.println(
                    "2.Create File"
            );

            System.out.println(
                    "3.Delete File"
            );

            System.out.println(
                    "4.Search File"
            );

            System.out.println(
                    "5.Restore File"
            );

            System.out.println(
                    "6.View Storage"
            );

            System.out.println(
                    "7.History"
            );

            System.out.println(
                    "8.Exit"
            );

            int ch =
                    sc.nextInt();

            sc.nextLine();

            switch (ch) {

                case 1:

                    System.out.print(
                            "Folder: "
                    );

                    fs.createFolder(
                            sc.nextLine()
                    );

                    break;

                case 2:

                    System.out.print(
                            "Folder: "
                    );

                    String folder =
                            sc.nextLine();

                    System.out.print(
                            "File: "
                    );

                    String file =
                            sc.nextLine();

                    System.out.print(
                            "Type: "
                    );

                    String type =
                            sc.nextLine();

                    System.out.print(
                            "Size: "
                    );

                    int size =
                            sc.nextInt();

                    fs.createFile(
                            folder,
                            file,
                            type,
                            size
                    );

                    break;

                case 3:

                    System.out.print(
                            "Folder: "
                    );

                    folder =
                            sc.nextLine();

                    System.out.print(
                            "File: "
                    );

                    file =
                            sc.nextLine();

                    fs.deleteFile(
                            folder,
                            file
                    );

                    break;

                case 4:

                    System.out.print(
                            "Search: "
                    );

                    fs.search(
                            sc.nextLine()
                    );

                    break;

                case 5:

                    fs.restore();

                    break;

                case 6:

                    fs.viewStorage();

                    break;

                case 7:

                    fs.showHistory();

                    break;

                case 8:

                    return;
            }
        }
    }
}