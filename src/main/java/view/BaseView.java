package view;

import utils.MyLinkedList;

import model.common.Listable;
import service.exception.ServiceException;
import service.exception.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    // #region Read Methods
    public String readString(String msg) {
        return ConsoleUtils.readString(msg, null);
    }

    public Long readLong(String msg) {
        return ConsoleUtils.readLong(msg, null);
    }

    public double readDouble(String msg) {
        return ConsoleUtils.readDouble(msg, null);
    }

    public LocalDate readData(String msg) {
        return ConsoleUtils.readData(msg, null);
    }

    public LocalDateTime readDateTime(String msg) {
        return ConsoleUtils.readDateTime(msg, null);
    }

    public String readStringDefault(String msg, String defaultValue) {
        return ConsoleUtils.readString(msg, defaultValue);
    }

    public Long readLongDefault(String msg, String defaultValue) {
        return ConsoleUtils.readLong(msg, defaultValue);
    }

    public double readDoubleDefault(String msg, String defaultValue) {
        return ConsoleUtils.readDouble(msg, defaultValue);
    }

    public LocalDate readDataDefault(String msg, String defaultValue) {
        return ConsoleUtils.readData(msg, defaultValue);
    }

    public LocalDateTime readDateTimeDefault(String msg, String defaultValue) {
        return ConsoleUtils.readDateTime(msg, defaultValue);
    }
    // #region Read Methods

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
