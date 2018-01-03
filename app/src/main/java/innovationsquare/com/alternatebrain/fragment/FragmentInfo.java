package innovationsquare.com.alternatebrain.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uncopt.android.widget.text.justify.JustifiedTextView;

import innovationsquare.com.alternatebrain.R;

/**
 * Created by tariq on 11/10/2017.
 */

public class FragmentInfo extends Fragment {

    EditText name, username, email, sector, city, country, phone;
    TextView changePassword;
    String usernameDummy;
    public View view;
    public RelativeLayout updateProfile;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_info, container, false);

//        myMsg.setText("This APP will be the second brain for \n1) Users: To do most of the decision making and calculations automatically like how to plan next month’s budget, where to find the cheapest product, how to save income tax etc. Now all above will be handled by this App. You just sit and relax. \n2) Corporate World: To see buying behavior/ pattern, selling trend, customer segmentation and categorization, footfall and rating, prediction and forecasting, target campaigning, Market Basket analysis, text analytics, link analysis and last but not the least Social Media Analytics. For more details, please email our support team at support_ab@alternatebrain.com");
//        JustifiedTextView jtv= new JustifiedTextView(getActivity(), "\n" +
//                "This APP will be the second brain for \n1) Users: To do most of the decision making and calculations automatically like how to plan next month’s budget, where to find the cheapest product, how to save income tax etc. Now all above will be handled by this App. You just sit and relax. 2) Corporate World: To see buying behavior/ pattern, selling trend, customer segmentation and categorization, footfall and rating, prediction and forecasting, target campaigning, Market Basket analysis, text analytics, link analysis and last but not the least Social Media Analytics. For more details, please email our support team at support_ab@alternatebrain.com");
//        LinearLayout place = (LinearLayout) view.findViewById(R.id.book_profile_content);
//        place.addView(jtv);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        JustifiedTextView heading = (JustifiedTextView)view.findViewById(R.id.heading);
        heading.setText(Html.fromHtml("<p style=\"margin-top: 0pt; margin-bottom: 0pt; margin-left: 0in; direction: ltr; unicode-bidi: embed; word-break: normal; text-align: center;\"><u style=\"text-underline: single;\"><strong><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #ed7d31; language: en-US;\">A MUST READ:</span></strong></u></p>"));

        JustifiedTextView myMsg = (JustifiedTextView)view.findViewById(R.id.t1);
        myMsg.setText(Html.fromHtml("<p style=\"margin-top: 0pt; margin-bottom: 0pt; margin-left: 0in; text-align: center; direction: ltr; unicode-bidi: embed; word-break: normal; punctuation-wrap: hanging;\"><strong><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">Save your </span></strong><strong><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">receipts, bills, fee vouchers, warrantee cards etc</span></strong><strong><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">.</span></strong></p>\n" +
                "<p style=\"margin-top: 0pt; margin-bottom: 0pt; margin-left: 0in; text-align: left; direction: ltr; unicode-bidi: embed; word-break: normal; punctuation-wrap: hanging;\"><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">Alternate </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">Brain, is the second brain for customers, </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">buyers </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">or </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">payers </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">of any walk in </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">life</span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">. It </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">will help them </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">offload basis activities, one do on daily basis like saving receipts, bills, fee vouchers, warrantee cards etc. which are an important part of </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">anyone's </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">life but not possible for everyone to keep track of</span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">.</span></p>\n" +
                //"<p style=\"margin-top: 0pt; margin-bottom: 0pt; margin-left: 0in; text-align: left; direction: ltr; unicode-bidi: embed; word-break: normal; punctuation-wrap: hanging;\"><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">Below </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">are the impact on your daily life, if you don&rsquo;t </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">save </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">of receipts, bills, fee vouchers, warrantee cards etc</span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">.:</span></p>\n" +
                //"<p style=\"margin-top: 0pt; margin-bottom: 0pt; margin-left: .19in; text-indent: -.19in; text-align: left; direction: ltr; unicode-bidi: embed; word-break: normal; punctuation-wrap: hanging;\"><span style=\"font-size: 12.0pt;\">-&nbsp;</span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">User is not able </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">to return or replace damaged item</span></p>\n" +
                //"<p style=\"margin-top: 0pt; margin-bottom: 0pt; margin-left: .19in; text-indent: -.19in; text-align: left; direction: ltr; unicode-bidi: embed; word-break: normal; punctuation-wrap: hanging;\"><span style=\"font-size: 12.0pt;\">-&nbsp;</span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">User is </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">not able to replace, just in case product size and color is wrong</span></p>\n" +
                //"<p style=\"margin-top: 0pt; margin-bottom: 0pt; margin-left: .19in; text-indent: -.19in; text-align: left; direction: ltr; unicode-bidi: embed; word-break: normal; punctuation-wrap: hanging;\"><span style=\"font-size: 12.0pt;\">-&nbsp;</span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">User is </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">not </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">able to return or replace, if, product gets damaged before warrantee </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">period</span></p>\n" +
                //"<p style=\"margin-top: 0pt; margin-bottom: 0pt; margin-left: .19in; text-indent: -.19in; text-align: left; direction: ltr; unicode-bidi: embed; word-break: normal; punctuation-wrap: hanging;\"><span style=\"font-size: 12.0pt;\">-&nbsp;</span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">User is </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">not </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">able to apply for official expense </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">reimbursement</span></p>\n" +
                //"<p style=\"margin-top: 0pt; margin-bottom: 0pt; margin-left: .19in; text-indent: -.19in; text-align: left; direction: ltr; unicode-bidi: embed; word-break: normal; punctuation-wrap: hanging;\"><span style=\"font-size: 12.0pt;\">-&nbsp;</span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">User is </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">not </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">able to remember when, from which shop, location and for what price, he/ she bought </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">what</span></p>\n" +
                //"<p style=\"margin-top: 0pt; margin-bottom: 0pt; margin-left: .19in; text-indent: -.19in; text-align: left; direction: ltr; unicode-bidi: embed; word-break: normal; punctuation-wrap: hanging;\"><span style=\"font-size: 12.0pt;\">-&nbsp;</span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">There are many other benefit user can avail using this app. Next release bring a lot more, so keep upload </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">receipts, bills, fee vouchers, warrantee cards etc</span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">. as much as possible.</span></p>\n" +
                "<p style=\"margin-top: 0pt; margin-bottom: 0pt; margin-left: 0in; text-align: left; direction: ltr; unicode-bidi: embed; word-break: normal; punctuation-wrap: hanging;\"><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">Note: You personal data or your </span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">receipts, bills, fee vouchers, warrantee cards etc</span><span style=\"font-size: 12.0pt; font-family: 'Century Gothic'; color: #002060; language: en-US;\">. will not be shared with anyone, at any cost. And you can always go in Profile and Unsubscribe/ Drop your account which will delete every piece of information you have on our server.</span></p>"));

    }
}