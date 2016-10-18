import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.uniroma1.lcl.babelnet.BabelAPIInfo;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelNetUtils;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.demo.BabelNetDemo;
import it.uniroma1.lcl.jlt.util.Language;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

/**
 */
public class SynsetGenerater {

    private static String articleFile = "/home/cuixuan/experiment/articles/TextPairs_product.txt";
    private static String synsetMapFile = "synSetMap_product.map";
    private static String wordIdSetFile = "wordToSynIds_product.dat";
    private static Map<String,String> idmap = new HashMap<String,String>();
    private static Map<String,Set<String>> synSetMap = new HashMap<String, Set<String>>();
    private static Set<String> zhwordSet = new HashSet<String>();
    private static Set<String> enwordSet = new HashSet<String>();
    private static boolean active = false;
    public static void main(String[] args) throws Exception {

        System.setProperty("http.proxyHost", "child-prc.intel.com");
        System.setProperty("http.proxyPort", "913");
        /*BabelNetDemo.testTranslations("business",Language.EN,Language.ZH,Language.EN);
        String result = sendPost("http://babelnet.io/v4/getSynsetIds","word=business&langs=EN&filterLangs=EN&filterLangs=ZH&key=def97dc0-aa52-402a-8632-af2c73b71718");
        System.out.println(result);

        BabelNet bn = BabelNet.getInstance();
        List<Language> filterLang = new ArrayList<Language>();
        filterLang.add(Language.EN);
        filterLang.add(Language.ZH);
        List<BabelSynset> byl = bn.getSynsets("business", Language.EN);
        for(BabelSynset set:byl){
            System.out.println(set);
        }*/
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(articleFile),"utf-8"));
        String line = null;
        int num=0;
        while((line = br.readLine())!=null){
            line = line.toLowerCase();
            String[] splits = line.split("@#@#@");
            String url = splits[0];
            String title = splits[1];
            String lang = splits[2];
            String[] zhsplits = splits[3].substring(1,splits[3].length()-1).split(", ");
            for(String zhsp:zhsplits){
                zhwordSet.add(zhsp);
            }

            String[] ensplits = splits[4].substring(1,splits[4].length()-1).split(", ");
            for(String ensp:ensplits){
                enwordSet.add(ensp);
            }

            List<Term> parse = NlpAnalysis.parse(title.toLowerCase());
            Set<String> set = lang.equals("en")?enwordSet:zhwordSet;
            for(Term term:parse){
                set.add(term.getName());
            }
        }
        br.close();
        System.out.println("Total readed Dictionary :"+(zhwordSet.size()+enwordSet.size()));
        loadId();
        BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(synsetMapFile,true),"utf-8"));
        for(String key:zhwordSet){
            Set<String> synIds = getTranslation(key,"ZH",bw1);
            System.out.print(String.format("\rFinished Words: %6d,  Current Word:%15s%10s",num+1,key,"EN"));
            for(String id:synIds){
                if(synSetMap.containsKey(id)){
                    Set<String> wordset = synSetMap.get(id);
                    wordset.add(key);
                    synSetMap.put(id,wordset);
                }else{
                    Set<String> wordset = new HashSet<String>();
                    wordset.add(key);
                    synSetMap.put(id,wordset);
                }
            }
            num++;
//            if(num%50==0) System.out.println();
            if(num%1000==0){
                System.out.print("\rWord Have Handled: "+num+"                                                          \n");
            }
//            if(num>3000)break;
        }
        for(String key:enwordSet){
            Set<String> synIds = getTranslation(key,"EN",bw1);
            System.out.print(String.format("\rFinished Words: %6d,  Current Word:%15s%10s",num+1,key,"ZH"));
            for(String id:synIds){
                if(synSetMap.containsKey(id)){
                    Set<String> wordset = synSetMap.get(id);
                    wordset.add(key);
                    synSetMap.put(id,wordset);
                }else{
                    Set<String> wordset = new HashSet<String>();
                    wordset.add(key);
                    synSetMap.put(id,wordset);
                }
            }
            num++;
//            if(num%50==0) System.out.println();
            if(num%1000==0){
                System.out.print("\rWord Have Handled: "+num+"                                                          \n");
            }
//            if(num>3000)break;
        }
        for(String key:zhwordSet){
            Set<String> synIds = getTranslation(key,"EN",bw1);
            System.out.print(String.format("\rFinished Words: %6d,  Current Word:%15s%10s",num+1,key,"EN"));
            for(String id:synIds){
                if(synSetMap.containsKey(id)){
                    Set<String> wordset = synSetMap.get(id);
                    wordset.add(key);
                    synSetMap.put(id,wordset);
                }else{
                    Set<String> wordset = new HashSet<String>();
                    wordset.add(key);
                    synSetMap.put(id,wordset);
                }
            }
            num++;
//            if(num%50==0) System.out.println();
            if(num%1000==0){
                System.out.print("\rWord Have Handled: "+num+"                                                          \n");
            }
//            if(num>3000)break;
        }
        for(String key:enwordSet){
            Set<String> synIds = getTranslation(key,"ZH",bw1);
            System.out.print(String.format("\rFinished Words: %6d,  Current Word:%15s%10s",num+1,key,"ZH"));
            for(String id:synIds){
                if(synSetMap.containsKey(id)){
                    Set<String> wordset = synSetMap.get(id);
                    wordset.add(key);
                    synSetMap.put(id,wordset);
                }else{
                    Set<String> wordset = new HashSet<String>();
                    wordset.add(key);
                    synSetMap.put(id,wordset);
                }
            }
            num++;
//            if(num%50==0) System.out.println();
            if(num%1000==0){
                System.out.print("\rWord Have Handled: "+num+"                                                          \n");
            }
//            if(num>3000)break;
        }
        bw1.close();

        BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(wordIdSetFile),"utf-8"));
        BufferedWriter bw3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tmpWordSetToIDSet.dat"),"utf-8"));
        for(String key:synSetMap.keySet()){
            Set<String> set = synSetMap.get(key);
            if(set.size()<2)continue;
            StringBuilder sb = new StringBuilder();
            Iterator<String> it = set.iterator();
            if (! it.hasNext()) {
                sb.append("{}");
            }else {
                sb.append('{');
                for (; ; ) {
                    String e = it.next();
                    sb.append(e);
                    if (!it.hasNext()){
                        sb.append('}');
                        break;
                    }
                    else sb.append(',').append(' ');
                }
            }
            bw2.write(sb.toString()+"\n");
        }
        bw2.close();
        bw3.close();
    }

    public static void loadId() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(synsetMapFile),"utf-8"));
        String line = null;
        while((line = br.readLine())!=null){
            if(line.length()<=0)break;
            String[] splits = line.split("@#@#@");
            idmap.put(splits[0]+"@#@#@"+splits[1],splits[2]);
        }
        System.out.println("Id readed:"+idmap.size());
        br.close();
    }

    public static Set<String> getTranslation(String word,String lang,BufferedWriter bw1) throws Exception {
        String url = "http://babelnet.io/v4/getSynsetIds";
        String newword = URLEncoder.encode(word);
        String param = "word="+newword+"&langs="+lang.toUpperCase()+"&filterLangs=EN&filterLangs=ZH&key=def97dc0-aa52-402a-8632-af2c73b71718";
        String result;
        String key = word+"@#@#@"+lang;
        if(!idmap.containsKey(key)) {
//            System.out.println(param);
            result = sendPost(url, param,true);
            result.replace("\n", "");
            bw1.write(word + "@#@#@" + lang + "@#@#@" + result + "\n");
            bw1.flush();
        }else{
            result = idmap.get(key);
        }
        JSONArray json = new JSONArray(result);
        Set<String> sets = new HashSet<String>();
        for(int i=0;i<json.length();i++){
            JSONObject object = json.getJSONObject(i);
            String id = object.getString("id");
            sets.add(id);
        }
        return sets;
    }
    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param,boolean retry) throws Exception {

        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("Connection", "Keep-Alive");
            if(!active) {
                conn.setRequestProperty("User-Agent", "Gecko/20100101 Firefox/45.0");
                conn.setRequestProperty("Accept-Language","en,zh;q=0.8,zh-CN;q=0.6");
            }else {
                conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:45.0)");
            }
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("\n发送 POST 请求出现异常！");
            if(retry){
                active = !active;
                result = sendPost(url,param,false);
            }else {
                throw e;
            }
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                throw ex;
            }
        }
        return result;
    }
}
