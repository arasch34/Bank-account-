;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class BankAccount {

    Map<String, String> DB = new HashMap<String, String>(); 
    public static final String SALT = "zebra";
    static int s[] = new int[256];
    static int k[] = new int[256];

    public static void main(String args[]) throws Exception{
        Scanner scan = new Scanner(System.in); 
        BankAccount run = new BankAccount();
        boolean Flag = false;

        System.out.print("Create new account: (username,password)");
        String a = scan.next();
        String b = scan.next();

        run.signup(a, b);
        while(!Flag){

            System.out.print("Please sign in: (username,password)");
            a = scan.next();
            b = scan.next();   

            // login should fail because of wrong password.
            if (run.login(a, b)){
                System.out.println("User login successful.");
                Flag = true;
            }
            else
                System.out.println("user login failed, Please try again.");
        }
        bank user = new bank();
        System.out.print("What can I help you with? (plaintext,key)");
        a = scan.next();
        b = scan.next();        
        String ciphertext = encrypt(a,b);

        System.out.println("plaintext encryption : " + ciphertext );

        String plaintext = decrypt(ciphertext,b);        

        System.out.println("Bob decrypts ciphertext with key : " + plaintext);       

        int i = user.getBalance();

        String s = Integer.toString(i);

        ciphertext = encrypt(s,b);

        System.out.println("Bob sends back encrypted balance " + ciphertext );

        plaintext = decrypt(ciphertext,b);

        System.out.println("Alice decrypts ciphertext balance : " + plaintext); 

        System.out.print("What can I help you with? (plaintext,key)");
        a = scan.next();
        b = scan.next(); 

        ciphertext = encrypt(a,b);

        System.out.println("plaintext encryption : " + ciphertext );

        plaintext = decrypt(ciphertext,b);        

        System.out.println("Bob decrypts ciphertext with key : " + plaintext);       

        user.deposit(500);

        i = user.getBalance();

        s = Integer.toString(i);

        ciphertext = encrypt(s,b);

        System.out.println("Bob sends back encrypted balance " + ciphertext );

        plaintext = decrypt(ciphertext,b);

        System.out.println("Alice decrypts ciphertext balance : " + plaintext);

    }
    
    public void signup(String username, String password) {
        String sPassword = SALT + password;
        String hPassword = hash(sPassword);
        DB.put(username, hPassword);
    }

    public Boolean login(String username, String password) {
        Boolean isTrue = false;
        
        String sPassword = SALT + password;
        String hPassword = hash(sPassword);

        String shPassword = DB.get(username);
        if(hPassword.equals(shPassword)){
            isTrue = true;
        }else{
            isTrue = false;
        }
        return isTrue;
    }

    public static String hash(String in) {
        StringBuilder h = new StringBuilder();

        try {
            MessageDigest s = MessageDigest.getInstance("SHA-1");
            byte[] bytes = s.digest(in.getBytes());
            char[] dig = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'a', 'b', 'c', 'd', 'e', 'f' };
            for (int i = 0; i < bytes.length; i++ ){
                byte b = bytes[i];
                h.append(dig[(b & 0xf0) >> 4]);
                h.append(dig[b & 0x0f]);
            }
        } catch (NoSuchAlgorithmException e) {
           
        }

        return h.toString();
    }

    public static void initialization(String key){

        for(int i=0; i<256 ;i++)
        {
            s[i]=i;
            k[i]=key.charAt(i%key.length());
        }        
        int j=0;
        for(int i=0; i<256 ;i++)
        {
            j=(j+s[i]+k[i])%256;
            int t=s[i];
            s[i]=s[j];
            s[j]=t;
        }
    }

    public static void keyGen() 
    {
        int i=0,j=0;
        for(int n=0 ;n<256 ;n++)
        {  
            i=(i+1)% 256;
            j=(j+s[i])% 256;
            int t=s[i];
            s[i]=s[j];
            s[j]=t;
            k[n]=(char)(s[(s[i]+s[j])% 256]);
        }
    }

    public static String keyForm()
    {
        StringBuilder b = new StringBuilder();
        for(int i=0 ;i<256 ;i++)
        {
            int l=k[i];
            String a = Integer.toBinaryString(l);
            while(a.length()<8)
            {
                a="0"+a;
            }
            b.append(a);
        }  
        return b.toString();
    }

    public static String xorBin(String t)
    {
        String n = t;
        StringBuilder b = new StringBuilder();
        for(int i=0 ;i<n.length() ;i++)
        {
            int c =(int)(n.charAt(i));
            String p =Integer.toBinaryString(c);
            while(p.length()<8)
            {
                p ="0"+ p;
            }
            b.append(p);
        }
        n = b.toString();
        return n;
    }

    public static String xorS (String t)
    {
        String n = t;

        StringBuilder b = new StringBuilder();
        for(int i=0; i<n.length() ;i=i+8)
        {
            int x = Integer.parseInt(n.substring(i, i+8), 2);
            b.append((char)(x));
        }
        n = b.toString();
        return n ;
    }
    
    public static String encrypt(String t,String kT) 
    {
        initialization(kT);
        keyGen();
        String k = keyForm();
        String p = xorBin(t);
        String c =""; 
        String sK ,sP; 
        for(int i=0 ;i< p.length() ;i=i+8)
        {
            sK = k.substring( i%256 , (i+8)%256);
            sP = p.substring(i, i+8);
            for(int j=0 ; j< 8 ; j++)
            {
                c = c +(sP.charAt(j)^sK.charAt(j));
            }
        }
        c = xorS(c);

        return c;
    }
    
    public static String decrypt(String t,String kT) 
    {
        initialization(kT);
        keyGen();
        String k = keyForm();
        String c = xorBin(t);
        String p =""; 
        String sK ,sC; 
        for(int i=0 ;i< c.length() ;i=i+8)
        {
            sK = k.substring( i%256 , (i+8)%256);
            sC = c.substring(i, i+8);
            for(int j=0 ; j< 8 ; j++)
            {
                p = p +(sC.charAt(j)^sK.charAt(j));
            }
        }
        p = xorS(p);
        return p;
    }

}
