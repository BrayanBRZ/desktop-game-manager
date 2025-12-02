package view;

import utils.MyLinkedList;

import model.common.Listable;
import service.exception.ServiceException;
import service.exception.ValidationException;
import core.Navigation;
import utils.ConsoleUtils;
import utils.MenuRenderer;

public abstract class BaseView {

    public int renderBanner(String... options) {
        ConsoleUtils.clearScreen();
        MenuRenderer.renderBanner(Navigation.getPath());
        MenuRenderer.renderOptions(options);
        return ConsoleUtils.readInteger("Escolha: ", null);
    }

    public String readString(String msg, String defaultValue) {
        return ConsoleUtils.readString(msg, defaultValue);
    }

    // #region Render Methods
    public <T extends Listable> void renderEntityList(MyLinkedList<T> entityList) {
        for (T e : entityList) {
            MenuRenderer.renderMessageLine("ID: " + e.getId() + " - Name: " + e.getName());
        }
    }

    public void renderMessage(String msg) {
        MenuRenderer.renderMessage(msg);
    }

    public void renderMessageLine(String msg) {
        MenuRenderer.renderMessageLine(msg);
    }

    public void renderError(String msg) {
        MenuRenderer.renderError(msg);
    }

    public void renderValidationException(ValidationException e) {
        MenuRenderer.renderValidationException(e);
    }

    public void renderServiceException(ServiceException e) {
        MenuRenderer.renderServiceException(e);
    }

    public void renderException(Exception e) {
        MenuRenderer.renderException(e);
    }
    // #endregion Render Methods
}
