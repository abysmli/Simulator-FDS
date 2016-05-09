package com.fds.simulator.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

public class DataBuffer {
    public static JSONArray data = new JSONArray(
            "[{'component_id':1,'component_desc':'Mikrokontroller','activition':'elektrisch','series':'MC','component_symbol':'MC','insert_date':'2015-08-29 16:38:03.0','component_name':'Kontrolle','type':'MC','status':'active'},{'component_id':2,'component_desc':'Die Heizung erwaermt das Wasser in Behaelter 101, falls der Fuellstand nicht zu niedrig ist.','activition':'elektrisch','series':'E104','component_symbol':'Heating_Tank_101','insert_date':'2015-08-29 16:38:03.0','component_name':'Heizung','type':'actor','status':'active'},{'component_id':3,'component_desc':'Der Sensor misst die Wassertemperatur in Behaelter 101. ','activition':'elektrisch','series':'B104','component_symbol':'Temperatur_Tank_B104','insert_date':'2015-08-29 16:40:26.0','component_name':'Temperatur Sensor','type':'sensor','status':'active'},{'component_id':4,'component_desc':'Behaelter Nr. 101','activition':'-','series':'Tank101','component_symbol':'Tank_101','insert_date':'2015-08-29 16:40:26.0','component_name':'Tank 101','type':'Tank','status':'active'},{'component_id':5,'component_desc':'Das Ventil steuert den Zufluss aus Behaelter 102 in Behaelter 101. ','activition':'elektrisch','series':'V102','component_symbol':'Ball_Valve_V102','insert_date':'2015-08-29 16:54:09.0','component_name':'Kugelhahn V102','type':'actor','status':'active'},{'component_id':6,'component_desc':'Kugelhahn ist geschlossen.','activition':'elektrisch','series':'S115','component_symbol':'Ball_Valve_Closed_S115','insert_date':'2015-08-29 16:54:09.0','component_name':'Kugelhahn S115','type':'schalter','status':'active'},{'component_id':7,'component_desc':'Kugelhahn ist geoeffnet. ','activition':'elektrisch','series':'S116','component_symbol':'Ball_Valve_Open_S116 ','insert_date':'2015-08-29 16:54:09.0','component_name':'Kugelhahn S116','type':'schalter','status':'active'},{'component_id':8,'component_desc':'Der Sensor befindet sich an der Oberseite des Behaelters 101 und misst die Entfernung bis zur Wasseroberflaeche. Dadurch wird der Fuellstand ermittelt.','activition':'elektrisch','series':'B101','component_symbol':'Sensor_Level_Tank_B101 ','insert_date':'2015-08-29 16:54:09.0','component_name':'Ultraschallsensor','type':'sensor','status':'active'},{'component_id':9,'component_desc':'Mittlerer Fuellstand Behaelter 101.','activition':'elektrisch','series':'S117','component_symbol':'Float_Switch_S117','insert_date':'2015-08-29 16:54:09.0','component_name':'Schwimmschalter 117','type':'schalter','status':'active'},{'component_id':10,'component_desc':'Maximaler Fuellstand Behaelter 101. Kugelhahn muss geschlossen sein. ','activition':'elektrisch','series':'B114','component_symbol':'Proximity_Switch_Max_Level_B114 ','insert_date':'2015-08-29 16:54:09.0','component_name':'kapazitiver Naeherungsschalter','type':'schalter','status':'active'},{'component_id':11,'component_desc':'Dies ist ein analoges Ventil, das den Durchfluss steuert, indem die Weite der Ventiloeffnung variabel einstellbar ist. ','activition':'elektrisch','series':'M106','component_symbol':'Proportional_Valve_V106 ','insert_date':'2015-08-29 16:54:09.0','component_name':'Proportionalwegeventil','type':'actor','status':'active'},{'component_id':12,'component_desc':'Pumpe Motor Nr. 101','activition':'actuator','series':'M101','component_symbol':'Motor_101','insert_date':'2015-08-29 16:54:09.0','component_name':'Pumpe Motor 101','type':'actor','status':'active'},{'component_id':13,'component_desc':'Die Pumpe kann in einem analogen oder digitalen Modus betrieben werden. ','activition':'elektrisch','series':'P101','component_symbol':'Pump_P101 ','insert_date':'2015-08-29 16:54:09.0','component_name':'Durchflusspumpe','type':'actor','status':'active'},{'component_id':14,'component_desc':'Der Sensor misst den Wasserdruck in den Rohren, der durch die Pumpe und das Proportionalventil (V106) erzeugt wird. ','activition':'elektrisch','series':'B103','component_symbol':'Pressure_Tank_B103 ','insert_date':'2015-08-29 16:54:09.0','component_name':'Wasserdrucksensor','type':'sensor','status':'active'},{'component_id':15,'component_desc':'Der Sensor befindet sich direkt hinter der Pumpe und misst den Wasserdurchfluss durch das Rohr. ','activition':'elektrisch','series':'B102','component_symbol':'Flow_Meter_Pump_B102 ','insert_date':'2015-08-29 16:54:09.0','component_name':'Wasserdurchflusssensor','type':'sensor','status':'active'},{'component_id':16,'component_desc':'Maximaler Fuellstand Behaelter 101. ','activition':'elektrisch','series':'S111','component_symbol':'Float_Switch_S111 ','insert_date':'2015-08-29 16:54:09.0','component_name':'Schwimmschalter 111','type':'schalter','status':'active'},{'component_id':17,'component_desc':'Mittlerer Fuellstand Behaelter 102. ','activition':'elektrisch','series':'S112','component_symbol':'Float_Switch_S112 ','insert_date':'2015-08-29 16:54:09.0','component_name':'Schwimmschalter 112','type':'schalter','status':'active'},{'component_id':18,'component_desc':'Minimaler Fuellstand Behaelter 101. Die Heizung kann ab hier nicht mehr verwendet werden. ','activition':'elektrisch','series':'B113','component_symbol':'Proximity_Switch_Min_Level_B113 ','insert_date':'2015-08-29 16:54:09.0','component_name':'kapazitiver Naeherungsschalter','type':'schalter','status':'active'},{'component_id':19,'component_desc':'Die relevante Kugelhahn, welche von Hand geoeffnet und geschlossen werden kann. ','activition':'manuell','series':'V109','component_symbol':'Ventil_109','insert_date':'2015-08-29 16:54:09.0','component_name':'Ventil 109','type':'ventil','status':'active'},{'component_id':20,'component_desc':'Das Ventil steuert den Zufluss der Luft.','activition':'elektrisch','series':'VAir','component_symbol':'Air_Valve','insert_date':'2015-08-29 16:54:09.0','component_name':'Manget Ventil','type':'actor','status':'active'},{'component_id':21,'component_desc':'Der Sensor misst den Luftdurchfluss in den Rohren.','activition':'elektrisch','series':'Air101','component_symbol':'Air_101','insert_date':'2015-08-29 16:54:09.0','component_name':'Luftdurchflusssensor','type':'sensor','status':'active'},{'component_id':22,'component_desc':'Der Sensor misst den Luftdruck in den Rohren, der durch den Kompressor erzeugt wird. ','activition':'elektrisch','series':'Air102','component_symbol':'Air_102','insert_date':'2015-08-29 16:54:09.0','component_name':'Luftdrucksensor','type':'sensor','status':'active'},{'component_id':23,'component_desc':'Der Kompressor erzeugt Luftdruck.','activition':'elektrisch','series':'KOMP','component_symbol':'KOMP','insert_date':'2015-08-29 16:54:09.0','component_name':'Kompressor','type':'actor','status':'active'},{'component_id':24,'component_desc':'Die relevante Kugelhahn, welche von Hand geoeffnet und geschlossen werden kann. ','activition':'manuell','series':'V104','component_symbol':'Ventil_104','insert_date':'2015-08-29 16:54:09.0','component_name':'Ventil 104','type':'ventil','status':'active'},{'component_id':25,'component_desc':'Die relevante Kugelhahn, welche von Hand geoeffnet und geschlossen werden kann. ','activition':'manuell','series':'V112','component_symbol':'Ventil_112','insert_date':'2015-11-07 13:49:33.0','component_name':'Ventil 112','type':'ventil','status':'active'},{'component_id':26,'component_desc':'Behaelter Nr. 102','activition':'-','series':'Tank102','component_symbol':'Tank_102','insert_date':'2015-11-12 15:46:35.0','component_name':'Tank 102','type':'-','status':'active'}]");
    public static JSONArray faultData = new JSONArray();
    public static JSONArray strategy = new JSONArray();
    public static List<Integer> deactivedFunction = new ArrayList<>();
}
