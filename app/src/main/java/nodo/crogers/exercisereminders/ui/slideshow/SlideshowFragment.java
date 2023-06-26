package nodo.crogers.exercisereminders.ui.slideshow;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import nodo.crogers.exercisereminders.R;
import nodo.crogers.exercisereminders.databinding.FragmentSlideshowBinding;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Spanned sourceCodeLinkHtml = Html.fromHtml(getString(R.string.sourceCodeLink), Html.FROM_HTML_MODE_LEGACY);
        TextView sourceCodeLink = root.findViewById(R.id.sourceCodeLinkTextView);
        sourceCodeLink.setText(sourceCodeLinkHtml);
        sourceCodeLink.setMovementMethod(LinkMovementMethod.getInstance());

        Spanned licenseLinkHtml = Html.fromHtml(getString(R.string.licenseLink), Html.FROM_HTML_MODE_LEGACY);
        TextView licenceLink = root.findViewById(R.id.licenseLinkTextView);
        licenceLink.setText(licenseLinkHtml);
        licenceLink.setMovementMethod(LinkMovementMethod.getInstance());

        Spanned reportAnIssueLinkHtml = Html.fromHtml(getString(R.string.reportAnIssueLink), Html.FROM_HTML_MODE_LEGACY);
        TextView reportAnIssueLink = root.findViewById(R.id.reportAnIssueLinkTextView);
        reportAnIssueLink.setText(reportAnIssueLinkHtml);
        reportAnIssueLink.setMovementMethod(LinkMovementMethod.getInstance());

        Spanned buyMeACoffeeLinkHtml = Html.fromHtml(getString(R.string.buyMeACoffeeLink), Html.FROM_HTML_MODE_LEGACY);
        TextView buyMeACoffeeLink = root.findViewById(R.id.buyMeACoffeeLinkTextView);
        buyMeACoffeeLink.setText(buyMeACoffeeLinkHtml);
        buyMeACoffeeLink.setMovementMethod(LinkMovementMethod.getInstance());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}