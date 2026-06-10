public class RunInput {
    public static void main(String args[]) throws Exception {
        if(args.length == 0)
            System.out.println("Usage: java -cp 'repo/*' RunInput -- 'myinput'");
        else
            Entrypoint.entrypoint(args[0]);
    }
}