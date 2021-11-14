package nio;

public class ServerThead {

    public static void main(String[] args) {
        SelectorThreadGroup boss = new SelectorThreadGroup(3);  //混杂模式
        boss.bind(9999);
    }
}
