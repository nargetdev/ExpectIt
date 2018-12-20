package net.sf.expectit;

/*
 * #%L
 * ExpectIt
 * %%
 * Copyright (C) 2016 Alexey Gavrilov and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static net.sf.expectit.matcher.Matchers.anyString;
import static net.sf.expectit.matcher.Matchers.contains;
import static net.sf.expectit.matcher.Matchers.regexp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.telnet.TelnetClient;

/**
 * A telnet client example showing weather forecast for a city.
 */
public class ubnt_telnet {

    // modify this variable based on whether or not we have a live UBNT system
    private static final String OPT = "PRACTICE";

    private static class PortPowerSample {
        double samples[] = new double[24];
    }

    public static void main(String[] args) throws IOException {
        PortPowerSample sample = new PortPowerSample();

        TelnetClient telnet = new TelnetClient();

        if (OPT == "UBNT"){
            telnet.connect("10.200.1.252");
            StringBuilder wholeBuffer = new StringBuilder();
            Expect expect = new ExpectBuilder()
                    .withOutput(telnet.getOutputStream())
                    .withInputs(telnet.getInputStream())
                    .withEchoOutput(wholeBuffer)
                    .withEchoInput(wholeBuffer)
                    .withExceptionOnFailure()
                    .build();

//        expect.expect(contains("Trying 10.200.1.254..."));
//        expect.expect(contains("Connected to 10.200.1.254."));
//        expect.expect(contains("Escape character is '^]'."));

            String printme;

            printme = expect.expect(contains("User:")).getInput();
            System.out.println(printme);
            expect.sendLine("ubnt");

            printme = expect.expect(contains("Password:")).getInput();
            System.out.println(printme);
            expect.sendLine("ubnt");

            expect.expect(contains("(UBNT EdgeSwitch) >"));

            expect.sendLine("enable");

            expect.expect(contains("(UBNT EdgeSwitch) #"));

            String poe_power_command1 = "show poe status 0/1-0/12";
            String poe_power_command13 = "show poe status 0/13-0/24";
//        String commands[] = { poe_power_command1, poe_power_command13 };
            String commands[] = { poe_power_command1};


            for(;;){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (String command : commands){
                    expect.sendLine(command);

                    // capture file list
                    List<String> items;
                    for (int i = -3; i < 13; i++){
                        String list = expect.expect(regexp("\n")).getBefore();
                        items = Arrays.asList(list.split("\\s* \\s*"));

                        System.out.println("Items: " + items);

                        String pattern = "\\d+/\\d+";
                        Pattern p = Pattern.compile(pattern);
                        Matcher m = p.matcher(list);
                        if (m.find()){
                            System.out.println("gotem" + list);
                        }


//                System.out.println("wattage: " + items.get(3));
//
//                if (items.size() >= 7){
//
//                    if (i >= 0){
//                        sample.samples[i] = Double.parseDouble(items.get(3));
//                    }
//                }

                    }

                    expect.expect(contains("(UBNT EdgeSwitch) #"));

                }
            }


        }
        if (OPT == "PRACTICE"){
            telnet.setDefaultPort(51234);
            telnet.connect("127.0.0.1");

            StringBuilder wholeBuffer = new StringBuilder();
            Expect expect = new ExpectBuilder()
                    .withOutput(telnet.getOutputStream())
                    .withInputs(telnet.getInputStream())
                    .withEchoOutput(wholeBuffer)
                    .withEchoInput(wholeBuffer)
                    .withExceptionOnFailure()
                    .build();

//        expect.expect(contains("Trying 10.200.1.254..."));
//        expect.expect(contains("Connected to 10.200.1.254."));
//        expect.expect(contains("Escape character is '^]'."));

            String printme;

            expect.sendLine("hi");

            printme = expect.expect(contains("Intf")).getInput();
            System.out.println(printme);
            String poe_power_command1 = "show poe status 0/1-0/12";
//        String commands[] = { poe_power_command1, poe_power_command13 };
            String commands[] = { poe_power_command1};


            for(;;){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (String command : commands){
                    expect.sendLine(command);

                    // capture file list
                    List<String> items;
                    for (int i = -3; i < 13; i++){
                        String list = expect.expect(regexp("\n")).getBefore();
                        items = Arrays.asList(list.split("\\s* \\s*"));

                        System.out.println("Items: " + items);

                        String pattern = "(\\d+)/(\\d+)";
                        Pattern p = Pattern.compile(pattern);
                        Matcher m = p.matcher(list);

                        if (m.find()){
                            System.out.println("gotem" + list);
                            System.out.println("Found value: " + m.group(0) );
                            System.out.println("Found value: " + m.group(1) ); // the 
                            System.out.println("Found value: " + m.group(2) );
                        }
//                System.out.println("wattage: " + items.get(3));
//
//                if (items.size() >= 7){
//
//                    if (i >= 0){
//                        sample.samples[i] = Double.parseDouble(items.get(3));
//                    }
//                }

                    }

                    expect.expect(contains("(UBNT EdgeSwitch) #"));

                }
            }



        }

//        expect.close();
    }
}

