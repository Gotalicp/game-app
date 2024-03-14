package com.example.game_app.ui.main.more

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.game_app.databinding.DialogPolicyBinding

class PolicyDialogFragment : DialogFragment() {
    private var _binding: DialogPolicyBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPolicyBinding.inflate(inflater, container, false)
        requireDialog().requestWindowFeature(Window.FEATURE_NO_TITLE)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme)
        requireDialog().window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            val unencodedHtml = "<h1>Privacy Policy</h1>\n" +
                    "<p>Last updated: March 14, 2024</p>\n" +
                    "<p>This Privacy Policy describes Our policies and procedures on the collection, use and disclosure of Your information when You use the Service and tells You about Your privacy rights and how the law protects You.</p>\n" +
                    "<p>We use Your Personal data to provide and improve the Service. By using the Service, You agree to the collection and use of information in accordance with this Privacy Policy. This Privacy Policy has been created with the help of the <a href=\"https://www.freeprivacypolicy.com/free-privacy-policy-generator/\" target=\"_blank\">Free Privacy Policy Generator</a>.</p>\n" +
                    "<h2>Interpretation and Definitions</h2>\n" +
                    "<h3>Interpretation</h3>\n" +
                    "<p>The words of which the initial letter is capitalized have meanings defined under the following conditions. The following definitions shall have the same meaning regardless of whether they appear in singular or in plural.</p>\n" +
                    "<h3>Definitions</h3>\n" +
                    "<p>For the purposes of this Privacy Policy:</p>\n" +
                    "<ul>\n" +
                    "<li>\n" +
                    "<p><strong>Account</strong> means a unique account created for You to access our Service or parts of our Service.</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p><strong>Affiliate</strong> means an entity that controls, is controlled by or is under common control with a party, where &quot;control&quot; means ownership of 50% or more of the shares, equity interest or other securities entitled to vote for election of directors or other managing authority.</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p><strong>Application</strong> refers to GameGrid, the software program provided by the Company.</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p><strong>Company</strong> (referred to as either &quot;the Company&quot;, &quot;We&quot;, &quot;Us&quot; or &quot;Our&quot; in this Agreement) refers to GameGrid.</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p><strong>Country</strong> refers to:  Bulgaria</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p><strong>Device</strong> means any device that can access the Service such as a computer, a cellphone or a digital tablet.</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p><strong>Personal Data</strong> is any information that relates to an identified or identifiable individual.</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p><strong>Service</strong> refers to the Application.</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p><strong>Service Provider</strong> means any natural or legal person who processes the data on behalf of the Company. It refers to third-party companies or individuals employed by the Company to facilitate the Service, to provide the Service on behalf of the Company, to perform services related to the Service or to assist the Company in analyzing how the Service is used.</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p><strong>Usage Data</strong> refers to data collected automatically, either generated by the use of the Service or from the Service infrastructure itself (for example, the duration of a page visit).</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p><strong>You</strong> means the individual accessing or using the Service, or the company, or other legal entity on behalf of which such individual is accessing or using the Service, as applicable.</p>\n" +
                    "</li>\n" +
                    "</ul>\n" +
                    "<h2>Collecting and Using Your Personal Data</h2>\n" +
                    "<h3>Types of Data Collected</h3>\n" +
                    "<h4>Personal Data</h4>\n" +
                    "<p>While using Our Service, We may ask You to provide Us with certain personally identifiable information that can be used to contact or identify You. Personally identifiable information may include, but is not limited to:</p>\n" +
                    "<ul>\n" +
                    "<li>\n" +
                    "<p>Email address</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p>Usage Data</p>\n" +
                    "</li>\n" +
                    "</ul>\n" +
                    "<h4>Usage Data</h4>\n" +
                    "<p>Usage Data is collected automatically when using the Service.</p>\n" +
                    "<p>Usage Data may include information such as Your Device's Internet Protocol address (e.g. IP address), browser type, browser version, the pages of our Service that You visit, the time and date of Your visit, the time spent on those pages, unique device identifiers and other diagnostic data.</p>\n" +
                    "<p>When You access the Service by or through a mobile device, We may collect certain information automatically, including, but not limited to, the type of mobile device You use, Your mobile device unique ID, the IP address of Your mobile device, Your mobile operating system, the type of mobile Internet browser You use, unique device identifiers and other diagnostic data.</p>\n" +
                    "<p>We may also collect information that Your browser sends whenever You visit our Service or when You access the Service by or through a mobile device.</p>\n" +
                    "<h4>Information Collected while Using the Application</h4>\n" +
                    "<p>While using Our Application, in order to provide features of Our Application, We may collect, with Your prior permission:</p>\n" +
                    "<ul>\n" +
                    "<li>Pictures and other information from your Device's camera and photo library</li>\n" +
                    "</ul>\n" +
                    "<p>We use this information to provide features of Our Service, to improve and customize Our Service. The information may be uploaded to the Company's servers and/or a Service Provider's server or it may be simply stored on Your device.</p>\n" +
                    "<p>You can enable or disable access to this information at any time, through Your Device settings.</p>\n" +
                    "<h3>Use of Your Personal Data</h3>\n" +
                    "<p>The Company may use Personal Data for the following purposes:</p>\n" +
                    "<ul>\n" +
                    "<li>\n" +
                    "<p><strong>To provide and maintain our Service</strong>, including to monitor the usage of our Service.</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p><strong>To manage Your Account:</strong> to manage Your registration as a user of the Service. The Personal Data You provide can give You access to different functionalities of the Service that are available to You as a registered user.</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p><strong>For the performance of a contract:</strong> the development, compliance and undertaking of the purchase contract for the products, items or services You have purchased or of any other contract with Us through the Service.</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p><strong>To contact You:</strong> To contact You by email, telephone calls, SMS, or other equivalent forms of electronic communication, such as a mobile application's push notifications regarding updates or informative communications related to the functionalities, products or contracted services, including the security updates, when necessary or reasonable for their implementation.</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p><strong>To provide You</strong> with news, special offers and general information about other goods, services and events which we offer that are similar to those that you have already purchased or enquired about unless You have opted not to receive such information.</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p><strong>To manage Your requests:</strong> To attend and manage Your requests to Us.</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p><strong>For business transfers:</strong> We may use Your information to evaluate or conduct a merger, divestiture, restructuring, reorganization, dissolution, or other sale or transfer of some or all of Our assets, whether as a going concern or as part of bankruptcy, liquidation, or similar proceeding, in which Personal Data held by Us about our Service users is among the assets transferred.</p>\n" +
                    "</li>\n" +
                    "<li>\n" +
                    "<p><strong>For other purposes</strong>: We may use Your information for other purposes, such as data analysis, identifying usage trends, determining the effectiveness of our promotional campaigns and to evaluate and improve our Service, products, services, marketing and your experience.</p>\n" +
                    "</li>\n" +
                    "</ul>\n" +
                    "<p>We may share Your personal information in the following situations:</p>\n" +
                    "<ul>\n" +
                    "<li><strong>With Service Providers:</strong> We may share Your personal information with Service Providers to monitor and analyze the use of our Service,  to contact You.</li>\n" +
                    "<li><strong>For business transfers:</strong> We may share or transfer Your personal information in connection with, or during negotiations of, any merger, sale of Company assets, financing, or acquisition of all or a portion of Our business to another company.</li>\n" +
                    "<li><strong>With Affiliates:</strong> We may share Your information with Our affiliates, in which case we will require those affiliates to honor this Privacy Policy. Affiliates include Our parent company and any other subsidiaries, joint venture partners or other companies that We control or that are under common control with Us.</li>\n" +
                    "<li><strong>With business partners:</strong> We may share Your information with Our business partners to offer You certain products, services or promotions.</li>\n" +
                    "<li><strong>With other users:</strong> when You share personal information or otherwise interact in the public areas with other users, such information may be viewed by all users and may be publicly distributed outside.</li>\n" +
                    "<li><strong>With Your consent</strong>: We may disclose Your personal information for any other purpose with Your consent.</li>\n" +
                    "</ul>\n" +
                    "<h3>Retention of Your Personal Data</h3>\n" +
                    "<p>The Company will retain Your Personal Data only for as long as is necessary for the purposes set out in this Privacy Policy. We will retain and use Your Personal Data to the extent necessary to comply with our legal obligations (for example, if we are required to retain your data to comply with applicable laws), resolve disputes, and enforce our legal agreements and policies.</p>\n" +
                    "<p>The Company will also retain Usage Data for internal analysis purposes. Usage Data is generally retained for a shorter period of time, except when this data is used to strengthen the security or to improve the functionality of Our Service, or We are legally obligated to retain this data for longer time periods.</p>\n" +
                    "<h3>Transfer of Your Personal Data</h3>\n" +
                    "<p>Your information, including Personal Data, is processed at the Company's operating offices and in any other places where the parties involved in the processing are located. It means that this information may be transferred to — and maintained on — computers located outside of Your state, province, country or other governmental jurisdiction where the data protection laws may differ than those from Your jurisdiction.</p>\n" +
                    "<p>Your consent to this Privacy Policy followed by Your submission of such information represents Your agreement to that transfer.</p>\n" +
                    "<p>The Company will take all steps reasonably necessary to ensure that Your data is treated securely and in accordance with this Privacy Policy and no transfer of Your Personal Data will take place to an organization or a country unless there are adequate controls in place including the security of Your data and other personal information.</p>\n" +
                    "<h3>Delete Your Personal Data</h3>\n" +
                    "<p>You have the right to delete or request that We assist in deleting the Personal Data that We have collected about You.</p>\n" +
                    "<p>Our Service may give You the ability to delete certain information about You from within the Service.</p>\n" +
                    "<p>You may update, amend, or delete Your information at any time by signing in to Your Account, if you have one, and visiting the account settings section that allows you to manage Your personal information. You may also contact Us to request access to, correct, or delete any personal information that You have provided to Us.</p>\n" +
                    "<p>Please note, however, that We may need to retain certain information when we have a legal obligation or lawful basis to do so.</p>\n" +
                    "<h3>Disclosure of Your Personal Data</h3>\n" +
                    "<h4>Business Transactions</h4>\n" +
                    "<p>If the Company is involved in a merger, acquisition or asset sale, Your Personal Data may be transferred. We will provide notice before Your Personal Data is transferred and becomes subject to a different Privacy Policy.</p>\n" +
                    "<h4>Law enforcement</h4>\n" +
                    "<p>Under certain circumstances, the Company may be required to disclose Your Personal Data if required to do so by law or in response to valid requests by public authorities (e.g. a court or a government agency).</p>\n" +
                    "<h4>Other legal requirements</h4>\n" +
                    "<p>The Company may disclose Your Personal Data in the good faith belief that such action is necessary to:</p>\n" +
                    "<ul>\n" +
                    "<li>Comply with a legal obligation</li>\n" +
                    "<li>Protect and defend the rights or property of the Company</li>\n" +
                    "<li>Prevent or investigate possible wrongdoing in connection with the Service</li>\n" +
                    "<li>Protect the personal safety of Users of the Service or the public</li>\n" +
                    "<li>Protect against legal liability</li>\n" +
                    "</ul>\n" +
                    "<h3>Security of Your Personal Data</h3>\n" +
                    "<p>The security of Your Personal Data is important to Us, but remember that no method of transmission over the Internet, or method of electronic storage is 100% secure. While We strive to use commercially acceptable means to protect Your Personal Data, We cannot guarantee its absolute security.</p>\n" +
                    "<h2>Children's Privacy</h2>\n" +
                    "<p>Our Service does not address anyone under the age of 13. We do not knowingly collect personally identifiable information from anyone under the age of 13. If You are a parent or guardian and You are aware that Your child has provided Us with Personal Data, please contact Us. If We become aware that We have collected Personal Data from anyone under the age of 13 without verification of parental consent, We take steps to remove that information from Our servers.</p>\n" +
                    "<p>If We need to rely on consent as a legal basis for processing Your information and Your country requires consent from a parent, We may require Your parent's consent before We collect and use that information.</p>\n" +
                    "<h2>Links to Other Websites</h2>\n" +
                    "<p>Our Service may contain links to other websites that are not operated by Us. If You click on a third party link, You will be directed to that third party's site. We strongly advise You to review the Privacy Policy of every site You visit.</p>\n" +
                    "<p>We have no control over and assume no responsibility for the content, privacy policies or practices of any third party sites or services.</p>\n" +
                    "<h2>Changes to this Privacy Policy</h2>\n" +
                    "<p>We may update Our Privacy Policy from time to time. We will notify You of any changes by posting the new Privacy Policy on this page.</p>\n" +
                    "<p>We will let You know via email and/or a prominent notice on Our Service, prior to the change becoming effective and update the &quot;Last updated&quot; date at the top of this Privacy Policy.</p>\n" +
                    "<p>You are advised to review this Privacy Policy periodically for any changes. Changes to this Privacy Policy are effective when they are posted on this page.</p>\n" +
                    "<h2>Contact Us</h2>\n" +
                    "<p>If you have any questions about this Privacy Policy, You can contact us:</p>\n" +
                    "<ul>\n" +
                    "<li>By email: Gotalicbgyt@gmail.com</li>\n" +
                    "</ul>"
            policy.loadData(
                Base64.encodeToString(unencodedHtml.toByteArray(), Base64.NO_PADDING),
                "text/html",
                "base64"
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "Privacy Policy"
    }
}