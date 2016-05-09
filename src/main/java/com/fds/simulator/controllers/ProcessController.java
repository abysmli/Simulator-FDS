/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fds.simulator.controllers;

import com.fds.simulator.guis.Gui;
import com.fds.simulator.guis.MenuGui;
import com.fds.simulator.utils.DataBuffer;
import com.fds.simulator.utils.ErrorLogger;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import org.json.JSONObject;

/**
 *
 * @author abysmli
 */
public class ProcessController {

    protected final int delay = 30;
    protected final double flowrateValve = 0.05;
    protected final double sollWaterLevel = 8;
    protected final double initWaterLevel = 2;
    protected final double sollWaterTemp = 28;
    protected final double initWaterTemp = 25;
    protected final double SumWaterLevel = 12;
    protected final double heatPower = 0.5;
    protected final double AirFlow = 7.0;
    protected final double initAirPressure = 0.0;
    protected final double sollAirPressure = 6.0;

    private double oldTemperature, oldWaterLevel, oldWaterPressure, oldWaterFlow, oldAirPressure, oldAirFlow,
            changeRateTemperature, changeRateWaterLevel, changeRateWaterPressure, changeRateWaterFlow,
            changeRateAirPressure, changeRateAirFlow;
    private double WaterTempIn102;

    private final Timer stopTimer, fillingTimer, heatingTimer, pumpingTimer, airpumpingTimer;
    protected long starttime;
    private boolean faultflag;

    protected final Gui gui;

    protected final MenuGui menuGui;

    protected final SimulatorCenterController simulatorCenterController;

    public ProcessController(SimulatorCenterController simulatorCenterController, Gui gui, MenuGui menuGui) {
        this.simulatorCenterController = simulatorCenterController;
        this.gui = gui;
        this.menuGui = menuGui;

        gui.setWaterLevel(initWaterLevel / SumWaterLevel);
        gui.setHeaterState(false);
        gui.setTemperatureDisplay(initWaterTemp);
        WaterTempIn102 = initWaterTemp;

        ActionListener stopTimerListener = (ActionEvent e) -> {
            if (gui.getTemperature() > 15.0) {
                gui.setTemperatureDisplay(gui.getTemperature() - 0.1 * delay / 1000);
            }
            if (gui.getAirPressure() > 0) {
                gui.setAirPressure(gui.getAirPressure() - 0.1 * delay / 3000);
            }
        };
        stopTimer = new Timer(delay, stopTimerListener);

        ActionListener fillingTimerListener = (ActionEvent e) -> {
            try {
                FillLowerTankProcess();
            } catch (Exception e1) {
                ErrorLogger.log(e1, "Error Exception", e1.getMessage(), ErrorLogger.ERROR_MESSAGE);
            }
            if (gui.getWaterLevel() >= sollWaterLevel / SumWaterLevel) {
                simulatorCenterController.Stop();
            }
        };
        fillingTimer = new Timer(delay, fillingTimerListener);

        ActionListener heatingTimerListener = (ActionEvent e) -> {
            if (gui.getTemperature() > sollWaterTemp) {
                simulatorCenterController.Stop();
            } else {
                try {
                    HeatProcess();
                } catch (Exception e1) {
                    ErrorLogger.log(e1, "Error Exception", e1.getMessage(), ErrorLogger.ERROR_MESSAGE);
                }
            }
        };
        heatingTimer = new Timer(delay, heatingTimerListener);

        ActionListener pumpingTimerListener = (ActionEvent e) -> {
            try {
                FillUpperTankProcess();
                if (gui.getWaterLevel() <= initWaterLevel / SumWaterLevel) {
                    simulatorCenterController.Stop();
                }
            } catch (Exception e1) {
                ErrorLogger.log(e1, "Error Exception", e1.getMessage(), ErrorLogger.ERROR_MESSAGE);
            }
        };
        pumpingTimer = new Timer(delay, pumpingTimerListener);

        ActionListener airpumpingTimerListener = (ActionEvent e) -> {
            if (gui.getAirPressure() > sollAirPressure) {
                simulatorCenterController.Stop();
            } else {
                AirPumpingProcess();
            }
        };
        airpumpingTimer = new Timer(delay, airpumpingTimerListener);
    }

    public void startAirPumpingProcess() {
        airpumpingTimer.start();
    }

    public void startCheckInitWaterLevelProcess() throws Exception {
        CheckInitWaterLevelProcess();
    }

    public void startCheckSollWaterLevelProcess() throws Exception {
        CheckSollWaterLevelProcess();
    }

    public void startCheckTemperaturProcess() throws Exception {
        CheckTemperaturProcess();
    }

    public void startFillLowerTankProcess() {
        fillingTimer.start();
    }

    public void startFillUpperTankProcess() {
        pumpingTimer.start();
    }

    public void startHeatProcess() {
        heatingTimer.start();
    }

    protected void AirPumpingProcess() {
        gui.setProcessLabelText("Process: Air Pumping");
        gui.setAirState(true);
        gui.setAirFlowRate(AirFlow);
        gui.setAirPressure(gui.getAirPressure() + AirFlow * delay / 10000);
        if (((System.currentTimeMillis()) - starttime) > 500) {
            calcChangeRate(System.currentTimeMillis() - starttime);
            DataBuffer.data.getJSONObject(0).put("value", "on");
            DataBuffer.data.getJSONObject(1).put("value", "off");
            DataBuffer.data.getJSONObject(2).put("value", String.valueOf(gui.getTemperature()));
            DataBuffer.data.getJSONObject(2).put("change_rate", String.valueOf(changeRateTemperature));
            DataBuffer.data.getJSONObject(3).put("value", "OK");
            DataBuffer.data.getJSONObject(4).put("value", "off");
            DataBuffer.data.getJSONObject(5).put("value", "off");
            DataBuffer.data.getJSONObject(6).put("value", "on");
            DataBuffer.data.getJSONObject(7).put("value", String.valueOf(gui.getWaterLevel()));
            DataBuffer.data.getJSONObject(7).put("change_rate", String.valueOf(changeRateWaterLevel));
            DataBuffer.data.getJSONObject(8).put("value", "off");
            DataBuffer.data.getJSONObject(9).put("value", "off");
            DataBuffer.data.getJSONObject(10).put("value", "on");
            DataBuffer.data.getJSONObject(11).put("value", "off");
            DataBuffer.data.getJSONObject(12).put("value", "off");
            DataBuffer.data.getJSONObject(13).put("value", String.valueOf(gui.getWaterPressure()));
            DataBuffer.data.getJSONObject(13).put("change_rate", String.valueOf(changeRateWaterPressure));
            DataBuffer.data.getJSONObject(14).put("value", String.valueOf(gui.getWaterFlow()));
            DataBuffer.data.getJSONObject(14).put("change_rate", String.valueOf(changeRateWaterFlow));
            DataBuffer.data.getJSONObject(15).put("value", "off");
            DataBuffer.data.getJSONObject(16).put("value", "off");
            DataBuffer.data.getJSONObject(17).put("value", "on");
            DataBuffer.data.getJSONObject(18).put("value", "on");
            DataBuffer.data.getJSONObject(19).put("value", "on");
            DataBuffer.data.getJSONObject(20).put("value", String.valueOf(gui.getAirFlow()));
            DataBuffer.data.getJSONObject(20).put("change_rate", String.valueOf(changeRateAirFlow));
            DataBuffer.data.getJSONObject(21).put("value", String.valueOf(gui.getAirPressure()));
            DataBuffer.data.getJSONObject(21).put("change_rate", String.valueOf(changeRateAirPressure));
            DataBuffer.data.getJSONObject(22).put("value", "on");
            DataBuffer.data.getJSONObject(23).put("value", "on");
            DataBuffer.data.getJSONObject(24).put("value", "on");
            DataBuffer.data.getJSONObject(25).put("value", "OK");
            JSONObject sendData = new JSONObject();
            sendData.put("components", DataBuffer.data);
            sendData.put("stamp_time", String.valueOf(System.currentTimeMillis()));
            sendData.put("process_id", new Integer(1));
            starttime = System.currentTimeMillis();
            simulatorCenterController.getWatchListGUI().refresh();
//            http.postComponentsValue(sendData);
        }

    }

    protected void FillLowerTankProcess() throws Exception {
        // fill lower tank
        gui.setProcessLabelText("Process: Filling");
        gui.setAperturePercentage(gui.getSliderValue());
        gui.setManualValveState(true);
        gui.setBallState(true);
        if (faultflag == false) {
            gui.setValveState(true);
        }
        if ((gui.getWaterLevel() < sollWaterLevel / SumWaterLevel) && (gui.getValveState() == true)
                && (gui.getManualValveState() == true)) {
            gui.setWaterLevel(gui.getWaterLevel() + (flowrateValve / 1000 * delay));
            gui.setShowWaterLevel1((double) Math.round(gui.getTankLevel() * 100) / 100);
            gui.setShowWaterLevel2((double) Math.round(gui.getWaterLevel() * 100) / 100);
            gui.setShowWaterTemp((double) Math.round((gui.getTemperature() * 10)) / 10);
        }
        gui.setAirState(false);
        gui.setAirFlowRate(0.0);
        if (((System.currentTimeMillis()) - starttime) > 500) {
            calcChangeRate(System.currentTimeMillis() - starttime);
            DataBuffer.data.getJSONObject(0).put("value", "on");
            DataBuffer.data.getJSONObject(1).put("value", "off");
            DataBuffer.data.getJSONObject(2).put("value", String.valueOf(gui.getTemperature()));
            DataBuffer.data.getJSONObject(2).put("change_rate", String.valueOf(changeRateTemperature));
            DataBuffer.data.getJSONObject(3).put("value", "OK");
            DataBuffer.data.getJSONObject(4).put("value", "on");
            DataBuffer.data.getJSONObject(5).put("value", "off");
            DataBuffer.data.getJSONObject(6).put("value", "on");
            DataBuffer.data.getJSONObject(7).put("value", String.valueOf(gui.getWaterLevel()));
            DataBuffer.data.getJSONObject(7).put("change_rate", String.valueOf(changeRateWaterLevel));
            DataBuffer.data.getJSONObject(8).put("value", "off");
            DataBuffer.data.getJSONObject(9).put("value", "off");
            DataBuffer.data.getJSONObject(10).put("value", "on");
            DataBuffer.data.getJSONObject(11).put("value", "off");
            DataBuffer.data.getJSONObject(12).put("value", "off");
            DataBuffer.data.getJSONObject(13).put("value", String.valueOf(gui.getWaterPressure()));
            DataBuffer.data.getJSONObject(13).put("change_rate", String.valueOf(changeRateWaterPressure));
            DataBuffer.data.getJSONObject(14).put("value", String.valueOf(gui.getWaterFlow()));
            DataBuffer.data.getJSONObject(14).put("change_rate", String.valueOf(changeRateWaterFlow));
            DataBuffer.data.getJSONObject(15).put("value", "off");
            DataBuffer.data.getJSONObject(16).put("value", "off");
            DataBuffer.data.getJSONObject(17).put("value", "on");
            DataBuffer.data.getJSONObject(18).put("value", "on");
            DataBuffer.data.getJSONObject(19).put("value", "on");
            DataBuffer.data.getJSONObject(20).put("value", String.valueOf(gui.getAirFlow()));
            DataBuffer.data.getJSONObject(20).put("change_rate", String.valueOf(changeRateAirFlow));
            DataBuffer.data.getJSONObject(21).put("value", String.valueOf(gui.getAirPressure()));
            DataBuffer.data.getJSONObject(21).put("change_rate", String.valueOf(changeRateAirPressure));
            DataBuffer.data.getJSONObject(22).put("value", "on");
            DataBuffer.data.getJSONObject(23).put("value", "on");
            DataBuffer.data.getJSONObject(24).put("value", "on");
            DataBuffer.data.getJSONObject(25).put("value", "OK");
            JSONObject sendData = new JSONObject();
            sendData.put("components", DataBuffer.data);
            sendData.put("stamp_time", String.valueOf(System.currentTimeMillis()));
            sendData.put("process_id", new Integer(2));
            starttime = System.currentTimeMillis();
            simulatorCenterController.getWatchListGUI().refresh();
//            http.postComponentsValue(sendData);
        }
    }

    // check Water Level for sollWaterLevel
    protected void CheckSollWaterLevelProcess() throws Exception {
        gui.setProcessLabelText("Process: Checking Filling");
        gui.setAperturePercentage(gui.getSliderValue());
        gui.setAirState(false);
        gui.setAirFlowRate(0.0);
        gui.setBallState(false);
        gui.setValveState(false);
        gui.setShowWaterLevel1((double) Math.round(gui.getTankLevel() * 100) / 100);
        gui.setShowWaterLevel2((double) Math.round(gui.getWaterLevel() * 100) / 100);
        gui.setShowWaterTemp((double) Math.round((gui.getTemperature() * 10)) / 10);
        calcChangeRate(System.currentTimeMillis() - starttime);
        DataBuffer.data.getJSONObject(0).put("value", "on");
        DataBuffer.data.getJSONObject(1).put("value", "off");
        DataBuffer.data.getJSONObject(2).put("value", String.valueOf(gui.getTemperature()));
        DataBuffer.data.getJSONObject(2).put("change_rate", String.valueOf(changeRateTemperature));
        DataBuffer.data.getJSONObject(3).put("value", "OK");
        DataBuffer.data.getJSONObject(4).put("value", "off");
        DataBuffer.data.getJSONObject(5).put("value", "on");
        DataBuffer.data.getJSONObject(6).put("value", "off");
        DataBuffer.data.getJSONObject(7).put("value", String.valueOf(gui.getWaterLevel()));
        DataBuffer.data.getJSONObject(7).put("change_rate", String.valueOf(changeRateWaterLevel));
        DataBuffer.data.getJSONObject(8).put("value", "on");
        DataBuffer.data.getJSONObject(9).put("value", "off");
        DataBuffer.data.getJSONObject(10).put("value", "on");
        DataBuffer.data.getJSONObject(11).put("value", "off");
        DataBuffer.data.getJSONObject(12).put("value", "off");
        DataBuffer.data.getJSONObject(13).put("value", String.valueOf(gui.getWaterPressure()));
        DataBuffer.data.getJSONObject(13).put("change_rate", String.valueOf(changeRateWaterPressure));
        DataBuffer.data.getJSONObject(14).put("value", String.valueOf(gui.getWaterFlow()));
        DataBuffer.data.getJSONObject(14).put("change_rate", String.valueOf(changeRateWaterFlow));
        DataBuffer.data.getJSONObject(15).put("value", "on");
        DataBuffer.data.getJSONObject(16).put("value", "off");
        DataBuffer.data.getJSONObject(17).put("value", "on");
        DataBuffer.data.getJSONObject(18).put("value", "on");
        DataBuffer.data.getJSONObject(19).put("value", "off");
        DataBuffer.data.getJSONObject(20).put("value", String.valueOf(gui.getAirFlow()));
        DataBuffer.data.getJSONObject(20).put("change_rate", String.valueOf(changeRateAirFlow));
        DataBuffer.data.getJSONObject(21).put("value", String.valueOf(gui.getAirPressure()));
        DataBuffer.data.getJSONObject(21).put("change_rate", String.valueOf(changeRateAirPressure));
        DataBuffer.data.getJSONObject(22).put("value", "off");
        DataBuffer.data.getJSONObject(23).put("value", "on");
        DataBuffer.data.getJSONObject(24).put("value", "on");
        DataBuffer.data.getJSONObject(25).put("value", "OK");
        JSONObject sendData = new JSONObject();
        sendData.put("components", DataBuffer.data);
        sendData.put("stamp_time", String.valueOf(System.currentTimeMillis()));
        sendData.put("process_id", new Integer(2));
        starttime = System.currentTimeMillis();
        simulatorCenterController.getWatchListGUI().refresh();
//        http.postComponentsValue(sendData);
    }

    protected void HeatProcess() throws Exception {
        gui.setProcessLabelText("Process: Heating");
        gui.setAperturePercentage(gui.getSliderValue());
        gui.setHeaterState(true);
        gui.setTemperatureDisplay(gui.getTemperature() + heatPower * delay / 1000);
        gui.setShowWaterLevel1((double) Math.round(gui.getTankLevel() * 100) / 100);
        gui.setShowWaterLevel2((double) Math.round(gui.getWaterLevel() * 100) / 100);
        gui.setShowWaterTemp((double) Math.round((gui.getTemperature() * 10)) / 10);
        gui.setAirState(false);
        gui.setAirFlowRate(0.0);
        if (((System.currentTimeMillis()) - starttime) > 500) {
            calcChangeRate(System.currentTimeMillis() - starttime);
            DataBuffer.data.getJSONObject(0).put("value", "on");
            DataBuffer.data.getJSONObject(1).put("value", "on");
            DataBuffer.data.getJSONObject(2).put("value", String.valueOf(gui.getTemperature()));
            DataBuffer.data.getJSONObject(2).put("change_rate", String.valueOf(changeRateTemperature));
            DataBuffer.data.getJSONObject(3).put("value", "OK");
            DataBuffer.data.getJSONObject(4).put("value", "off");
            DataBuffer.data.getJSONObject(5).put("value", "off");
            DataBuffer.data.getJSONObject(6).put("value", "on");
            DataBuffer.data.getJSONObject(7).put("value", String.valueOf(gui.getWaterLevel()));
            DataBuffer.data.getJSONObject(7).put("change_rate", String.valueOf(changeRateWaterLevel));
            DataBuffer.data.getJSONObject(8).put("value", "off");
            DataBuffer.data.getJSONObject(9).put("value", "off");
            DataBuffer.data.getJSONObject(10).put("value", "on");
            DataBuffer.data.getJSONObject(11).put("value", "off");
            DataBuffer.data.getJSONObject(12).put("value", "off");
            DataBuffer.data.getJSONObject(13).put("value", String.valueOf(gui.getWaterPressure()));
            DataBuffer.data.getJSONObject(13).put("change_rate", String.valueOf(changeRateWaterPressure));
            DataBuffer.data.getJSONObject(14).put("value", String.valueOf(gui.getWaterFlow()));
            DataBuffer.data.getJSONObject(14).put("change_rate", String.valueOf(changeRateWaterFlow));
            DataBuffer.data.getJSONObject(15).put("value", "off");
            DataBuffer.data.getJSONObject(16).put("value", "off");
            DataBuffer.data.getJSONObject(17).put("value", "on");
            DataBuffer.data.getJSONObject(18).put("value", "on");
            DataBuffer.data.getJSONObject(19).put("value", "off");
            DataBuffer.data.getJSONObject(20).put("value", String.valueOf(gui.getAirFlow()));
            DataBuffer.data.getJSONObject(20).put("change_rate", String.valueOf(changeRateAirFlow));
            DataBuffer.data.getJSONObject(21).put("value", String.valueOf(gui.getAirPressure()));
            DataBuffer.data.getJSONObject(21).put("change_rate", String.valueOf(changeRateAirPressure));
            DataBuffer.data.getJSONObject(22).put("value", "off");
            DataBuffer.data.getJSONObject(23).put("value", "on");
            DataBuffer.data.getJSONObject(24).put("value", "on");
            DataBuffer.data.getJSONObject(25).put("value", "OK");
            JSONObject sendData = new JSONObject();
            sendData.put("components", DataBuffer.data);
            sendData.put("stamp_time", String.valueOf(System.currentTimeMillis()));
            sendData.put("process_id", new Integer(3));
            starttime = System.currentTimeMillis();
            simulatorCenterController.getWatchListGUI().refresh();
//            http.postComponentsValue(sendData);
        }

    }

    protected void CheckTemperaturProcess() throws Exception {
        gui.setProcessLabelText("Process: Checking Temperature");
        gui.setAperturePercentage(gui.getSliderValue());
        gui.setHeaterState(false);
        gui.setPumpState(true);
        gui.setShowWaterLevel1((double) Math.round(gui.getTankLevel() * 100) / 100);
        gui.setShowWaterLevel2((double) Math.round(gui.getWaterLevel() * 100) / 100);
        gui.setShowWaterTemp((double) Math.round((gui.getTemperature() * 10)) / 10);
        gui.setAirState(false);
        gui.setAirFlowRate(0.0);
        calcChangeRate(System.currentTimeMillis() - starttime);
        DataBuffer.data.getJSONObject(0).put("value", "on");
        DataBuffer.data.getJSONObject(1).put("value", "off");
        DataBuffer.data.getJSONObject(2).put("value", String.valueOf(gui.getTemperature()));
        DataBuffer.data.getJSONObject(2).put("change_rate", String.valueOf(changeRateTemperature));
        DataBuffer.data.getJSONObject(3).put("value", "OK");
        DataBuffer.data.getJSONObject(4).put("value", "off");
        DataBuffer.data.getJSONObject(5).put("value", "on");
        DataBuffer.data.getJSONObject(6).put("value", "off");
        DataBuffer.data.getJSONObject(7).put("value", String.valueOf(gui.getWaterLevel()));
        DataBuffer.data.getJSONObject(7).put("change_rate", String.valueOf(changeRateWaterLevel));
        DataBuffer.data.getJSONObject(8).put("value", "on");
        DataBuffer.data.getJSONObject(9).put("value", "off");
        DataBuffer.data.getJSONObject(10).put("value", "on");
        DataBuffer.data.getJSONObject(11).put("value", "off");
        DataBuffer.data.getJSONObject(12).put("value", "off");
        DataBuffer.data.getJSONObject(13).put("value", String.valueOf(gui.getWaterPressure()));
        DataBuffer.data.getJSONObject(13).put("change_rate", String.valueOf(changeRateWaterPressure));
        DataBuffer.data.getJSONObject(14).put("value", String.valueOf(gui.getWaterFlow()));
        DataBuffer.data.getJSONObject(14).put("change_rate", String.valueOf(changeRateWaterFlow));
        DataBuffer.data.getJSONObject(15).put("value", "on");
        DataBuffer.data.getJSONObject(16).put("value", "off");
        DataBuffer.data.getJSONObject(17).put("value", "on");
        DataBuffer.data.getJSONObject(18).put("value", "on");
        DataBuffer.data.getJSONObject(19).put("value", "off");
        DataBuffer.data.getJSONObject(20).put("value", String.valueOf(gui.getAirFlow()));
        DataBuffer.data.getJSONObject(20).put("change_rate", String.valueOf(changeRateAirFlow));
        DataBuffer.data.getJSONObject(21).put("value", String.valueOf(gui.getAirPressure()));
        DataBuffer.data.getJSONObject(21).put("change_rate", String.valueOf(changeRateAirPressure));
        DataBuffer.data.getJSONObject(22).put("value", "off");
        DataBuffer.data.getJSONObject(23).put("value", "on");
        DataBuffer.data.getJSONObject(24).put("value", "on");
        DataBuffer.data.getJSONObject(25).put("value", "OK");
        JSONObject sendData = new JSONObject();
        sendData.put("components", DataBuffer.data);
        sendData.put("stamp_time", String.valueOf(System.currentTimeMillis()));
        sendData.put("process_id", new Integer(3));
        starttime = System.currentTimeMillis();
        simulatorCenterController.getWatchListGUI().refresh();
//        http.postComponentsValue(sendData);
    }

    protected void FillUpperTankProcess() throws Exception {
        gui.setProcessLabelText("Process: Pumping");
        double flowrate = gui.getSliderValue();
        if (flowrate == 0.0) {
            gui.setPumpMotorState(false);
            gui.setFlowRate(0.0);
            gui.setPressureRate(0.0);
            flowrate = gui.getSliderValue();
            gui.setAperturePercentage(gui.getSliderValue());
        } else {
            gui.setPumpMotorState(true);
            flowrate = gui.getSliderValue();
            gui.setAperturePercentage(gui.getSliderValue());
        }

        gui.setFlowRate(Math.round((flowrate * 100.0) / 10.0));
        gui.setPressureRate(Math.round((flowrate * 600.0) / 10.0));
        gui.setAperturePercentage(gui.getSliderValue());
        gui.setWaterLevel(gui.getWaterLevel() - ((flowrate / 10) / 1000 * delay));
//        WaterTempIn102 = (WaterTempIn102 * (1 - gui.getTankLevel())
//                + gui.getTemperature() * (flowrateValve / 1000) * delay)
//                / (((flowrateValve / 1000) * delay) + 1 - gui.getTankLevel());
//        gui.setTemperatureDisplay(gui.getTemperature() - 0.1 * delay / 1000);
        gui.setShowWaterLevel1((double) Math.round(gui.getTankLevel() * 100) / 100);
        gui.setShowWaterLevel2((double) Math.round(gui.getWaterLevel() * 100) / 100);
        gui.setShowWaterTemp((double) Math.round((gui.getTemperature() * 10)) / 10);
//        if (gui.getTemperature() < 0.0) {
//            gui.setTemperatureDisplay(0.0);
//            gui.setShowWaterLevel1((double) Math.round(gui.getTankLevel() * 100) / 100);
//            gui.setShowWaterLevel2((double) Math.round(gui.getWaterLevel() * 100) / 100);
//            gui.setShowWaterTemp((double) Math.round((gui.getTemperature() * 10)) / 10);
//        }
        gui.setAirState(false);
        gui.setAirFlowRate(0.0);
        if (((System.currentTimeMillis()) - starttime) > 2000) {
            calcChangeRate(System.currentTimeMillis() - starttime);
            DataBuffer.data.getJSONObject(0).put("value", "on");
            DataBuffer.data.getJSONObject(1).put("value", "off");
            DataBuffer.data.getJSONObject(2).put("value", String.valueOf(gui.getTemperature()));
            DataBuffer.data.getJSONObject(2).put("change_rate", String.valueOf(changeRateTemperature));
            DataBuffer.data.getJSONObject(3).put("value", "OK");
            DataBuffer.data.getJSONObject(4).put("value", "off");
            DataBuffer.data.getJSONObject(5).put("value", "off");
            DataBuffer.data.getJSONObject(6).put("value", "on");
            DataBuffer.data.getJSONObject(7).put("value", String.valueOf(gui.getWaterLevel()));
            DataBuffer.data.getJSONObject(7).put("change_rate", String.valueOf(changeRateWaterLevel));
            DataBuffer.data.getJSONObject(8).put("value", "off");
            DataBuffer.data.getJSONObject(9).put("value", "off");
            DataBuffer.data.getJSONObject(10).put("value", "on");
            DataBuffer.data.getJSONObject(11).put("value", "on");
            DataBuffer.data.getJSONObject(12).put("value", "on");
            DataBuffer.data.getJSONObject(13).put("value", String.valueOf(gui.getWaterPressure()));
            DataBuffer.data.getJSONObject(13).put("change_rate", String.valueOf(changeRateWaterPressure));
            DataBuffer.data.getJSONObject(14).put("value", String.valueOf(gui.getWaterFlow()));
            DataBuffer.data.getJSONObject(14).put("change_rate", String.valueOf(changeRateWaterFlow));
            DataBuffer.data.getJSONObject(15).put("value", "off");
            DataBuffer.data.getJSONObject(16).put("value", "off");
            DataBuffer.data.getJSONObject(17).put("value", "on");
            DataBuffer.data.getJSONObject(18).put("value", "on");
            DataBuffer.data.getJSONObject(19).put("value", "off");
            DataBuffer.data.getJSONObject(20).put("value", String.valueOf(gui.getAirFlow()));
            DataBuffer.data.getJSONObject(20).put("change_rate", String.valueOf(changeRateAirFlow));
            DataBuffer.data.getJSONObject(21).put("value", String.valueOf(gui.getAirPressure()));
            DataBuffer.data.getJSONObject(21).put("change_rate", String.valueOf(changeRateAirPressure));
            DataBuffer.data.getJSONObject(22).put("value", "off");
            DataBuffer.data.getJSONObject(23).put("value", "on");
            DataBuffer.data.getJSONObject(24).put("value", "on");
            DataBuffer.data.getJSONObject(25).put("value", "OK");
            JSONObject sendData = new JSONObject();
            sendData.put("components", DataBuffer.data);
            sendData.put("stamp_time", String.valueOf(System.currentTimeMillis()));
            sendData.put("process_id", new Integer(4));
            starttime = System.currentTimeMillis();
            simulatorCenterController.getWatchListGUI().refresh();
//            http.postComponentsValue(sendData);
        }
    }

    // check water level for initWaterLevel
    protected void CheckInitWaterLevelProcess() throws Exception {
        gui.setProcessLabelText("Process: Checking WaterLevel");
        gui.setAperturePercentage(gui.getSliderValue());
        gui.setPumpState(false);
        gui.setFlowRate(0.0);
        gui.setPressureRate(0.0);
        gui.setAirState(false);
        gui.setAirFlowRate(0.0);
        gui.setShowWaterLevel1((double) Math.round(gui.getTankLevel() * 100) / 100);
        gui.setShowWaterLevel2((double) Math.round(gui.getWaterLevel() * 100) / 100);
        gui.setShowWaterTemp((double) Math.round((gui.getTemperature() * 10)) / 10);
        calcChangeRate(System.currentTimeMillis() - starttime);
        DataBuffer.data.getJSONObject(0).put("value", "on");
        DataBuffer.data.getJSONObject(1).put("value", "off");
        DataBuffer.data.getJSONObject(2).put("value", String.valueOf(gui.getTemperature()));
        DataBuffer.data.getJSONObject(2).put("change_rate", String.valueOf(changeRateTemperature));
        DataBuffer.data.getJSONObject(3).put("value", "OK");
        DataBuffer.data.getJSONObject(4).put("value", "off");
        DataBuffer.data.getJSONObject(5).put("value", "on");
        DataBuffer.data.getJSONObject(6).put("value", "off");
        DataBuffer.data.getJSONObject(7).put("value", String.valueOf(gui.getWaterLevel()));
        DataBuffer.data.getJSONObject(7).put("change_rate", String.valueOf(changeRateWaterLevel));
        DataBuffer.data.getJSONObject(8).put("value", "on");
        DataBuffer.data.getJSONObject(9).put("value", "off");
        DataBuffer.data.getJSONObject(10).put("value", "off");
        DataBuffer.data.getJSONObject(11).put("value", "off");
        DataBuffer.data.getJSONObject(12).put("value", "off");
        DataBuffer.data.getJSONObject(13).put("value", String.valueOf(gui.getWaterPressure()));
        DataBuffer.data.getJSONObject(13).put("change_rate", String.valueOf(changeRateWaterPressure));
        DataBuffer.data.getJSONObject(14).put("value", String.valueOf(gui.getWaterFlow()));
        DataBuffer.data.getJSONObject(14).put("change_rate", String.valueOf(changeRateWaterFlow));
        DataBuffer.data.getJSONObject(15).put("value", "off");
        DataBuffer.data.getJSONObject(16).put("value", "on");
        DataBuffer.data.getJSONObject(17).put("value", "on");
        DataBuffer.data.getJSONObject(18).put("value", "on");
        DataBuffer.data.getJSONObject(19).put("value", "off");
        DataBuffer.data.getJSONObject(20).put("value", String.valueOf(gui.getAirFlow()));
        DataBuffer.data.getJSONObject(20).put("change_rate", String.valueOf(changeRateAirFlow));
        DataBuffer.data.getJSONObject(21).put("value", String.valueOf(gui.getAirPressure()));
        DataBuffer.data.getJSONObject(21).put("change_rate", String.valueOf(changeRateAirPressure));
        DataBuffer.data.getJSONObject(22).put("value", "off");
        DataBuffer.data.getJSONObject(23).put("value", "on");
        DataBuffer.data.getJSONObject(24).put("value", "on");
        DataBuffer.data.getJSONObject(25).put("value", "OK");
        JSONObject sendData = new JSONObject();
        sendData.put("components", DataBuffer.data);
        sendData.put("stamp_time", String.valueOf(System.currentTimeMillis()));
        sendData.put("process_id", new Integer(4));
        starttime = System.currentTimeMillis();
        simulatorCenterController.getWatchListGUI().refresh();
//        http.postComponentsValue(sendData);
    }

    protected void calcChangeRate(long timeDiff) {
        changeRateTemperature = (gui.getTemperature() - oldTemperature) * 1000 / timeDiff;
        changeRateWaterLevel = (gui.getWaterLevel() - oldWaterLevel) * 1000 / timeDiff;
        changeRateWaterPressure = (gui.getWaterPressure() - oldWaterPressure) * 1000 / timeDiff;
        changeRateWaterFlow = (gui.getWaterFlow() - oldWaterFlow) * 1000 / timeDiff;
        changeRateAirPressure = (gui.getAirPressure() - oldAirPressure) * 1000 / timeDiff;
        changeRateAirFlow = (gui.getAirFlow() - oldAirFlow) * 1000 / timeDiff;
        oldTemperature = gui.getTemperature();
        oldWaterLevel = gui.getWaterLevel();
        oldWaterPressure = gui.getWaterPressure();
        oldWaterFlow = gui.getWaterFlow();
        oldAirPressure = gui.getAirPressure();
        oldAirFlow = gui.getAirFlow();
    }

    public void stop() {
        stopTimer.start();
        fillingTimer.stop();
        heatingTimer.stop();
        pumpingTimer.stop();
        airpumpingTimer.stop();

        gui.setProcessLabelText("Stop");
        gui.setPumpState(false);
        gui.setValveState(false);
        gui.setAirState(false);
        gui.setHeaterState(false);
        gui.setBallState(false);
        gui.setAirFlowRate(0.0);
        gui.setFlowRate(0.0);
        gui.setPressureRate(0.0);
    }

    public void reset() {
        stop();
        faultflag = false;

        gui.setWaterLevel(initWaterLevel / SumWaterLevel);
        gui.setTemperatureDisplay(initWaterTemp);
        gui.setAirPressure(initAirPressure);

        if (stopTimer.isRunning()) {
            stopTimer.stop();
        }
    }

    public void setFault(boolean flag) {
        faultflag = flag;
    }
}