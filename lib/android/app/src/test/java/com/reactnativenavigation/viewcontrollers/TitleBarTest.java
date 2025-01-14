package com.reactnativenavigation.viewcontrollers;

import android.app.Activity;
import androidx.widget.ActionMenuView;
import android.view.View;
import android.widget.TextView;

import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.parse.params.Button;
import com.reactnativenavigation.parse.params.Text;
import com.reactnativenavigation.react.Constants;
import com.reactnativenavigation.react.ReactView;
import com.reactnativenavigation.utils.CollectionUtils;
import com.reactnativenavigation.views.titlebar.TitleBar;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.reactnativenavigation.utils.TitleBarHelper.createButtonController;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TitleBarTest extends BaseTest {

    private TitleBar uut;
    private Button leftButton;
    private Button textButton;
    private Button customButton;
    private Activity activity;

    @Override
    public void beforeEach() {
        activity = newActivity();
        createButtons();
        uut = spy(new TitleBar(activity));
    }

    private void createButtons() {
        leftButton = new Button();
        leftButton.id = Constants.BACK_BUTTON_ID;

        textButton = new Button();
        textButton.id = "textButton";
        textButton.text = new Text("Btn");

        customButton = new Button();
        customButton.id = "customBtn";
        customButton.component.name = new Text("com.rnn.customBtn");
        customButton.component.componentId = new Text("component4");
    }

    @Test
    public void setButton_setsTextButton() {
        uut.setRightButtons(rightButtons(textButton));
        uut.setLeftButtons(leftButton(leftButton));
        assertThat(uut.getMenu().getItem(0).getTitle()).isEqualTo(textButton.text.get());
    }

    @Test
    public void setButton_setsCustomButton() {
        uut.setLeftButtons(leftButton(leftButton));
        uut.setRightButtons(rightButtons(customButton));
        ReactView btnView = (ReactView) uut.getMenu().getItem(0).getActionView();
        assertThat(btnView.getComponentName()).isEqualTo(customButton.component.name.get());
    }

    @Test
    public void setRightButtons_emptyButtonsListClearsRightButtons() {
        uut.setLeftButtons(new ArrayList<>());
        uut.setRightButtons(rightButtons(customButton, textButton));
        uut.setLeftButtons(new ArrayList<>());
        uut.setRightButtons(new ArrayList<>());
        assertThat(uut.getMenu().size()).isEqualTo(0);
    }

    @Test
    public void setLeftButtons_emptyButtonsListClearsLeftButton() {
        uut.setLeftButtons(leftButton(leftButton));
        uut.setRightButtons(rightButtons(customButton));
        assertThat(uut.getNavigationIcon()).isNotNull();

        uut.setLeftButtons(new ArrayList<>());
        uut.setRightButtons(rightButtons(textButton));
        assertThat(uut.getNavigationIcon()).isNull();
    }

    @Test
    public void setLeftButton_titleIsAligned() {
        uut.setTitle("Title");
        TextView title = new TextView(activity);
        uut.addView(title);
        when(uut.findTitleTextView()).thenReturn(title);

        uut.setLeftButtons(Collections.singletonList(Mockito.mock(TitleBarButtonController.class)));
        dispatchPreDraw(title);
        verify(uut).alignTextView(any(), eq(title));
    }

    @Test
    public void setRightButtons_buttonsAreAddedInReverseOrderToMatchOrderOnIOs() {
        uut.setLeftButtons(new ArrayList<>());
        uut.setRightButtons(rightButtons(textButton, customButton));
        assertThat(uut.getMenu().getItem(1).getTitle()).isEqualTo(textButton.text.get());
    }

    @Test
    public void setComponent_addsComponentToTitleBar() {
        View component = new View(activity);
        uut.setComponent(component);
        verify(uut).addView(component);
    }

    @Test
    public void addView_overflowIsEnabledForButtonsContainer() {
        ActionMenuView buttonsContainer = mock(ActionMenuView.class);
        uut.addView(buttonsContainer);
        verify(buttonsContainer).setClipChildren(false);
    }

    @Test
    public void clear() {
        View title = new View(activity);
        uut.setComponent(title);
        verify(uut).addView(title);

        uut.clear();
        assertThat(uut.getTitle()).isNullOrEmpty();
        assertThat(uut.getMenu().size()).isZero();
        assertThat(uut.getNavigationIcon()).isNull();
        verify(uut).removeView(title);
    }

    private List<TitleBarButtonController> leftButton(Button leftButton) {
        return Collections.singletonList(createButtonController(activity, uut, leftButton));
    }

    private List<TitleBarButtonController> rightButtons(Button... buttons) {
        return CollectionUtils.map(Arrays.asList(buttons), button -> createButtonController(activity, uut, button));
    }
}
