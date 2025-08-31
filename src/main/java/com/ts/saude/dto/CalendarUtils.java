package com.ts.saude.dto;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.ts.saude.model.CalendarDay;

public class CalendarUtils {

    // public static List<List<CalendarDay>> generateCalendar(LocalDate referencia)
    // {
    // List<List<CalendarDay>> weeks = new ArrayList<>();

    // LocalDate primeiroDiaMes = referencia.withDayOfMonth(1);
    // LocalDate inicioCalendario = primeiroDiaMes.with(DayOfWeek.SUNDAY);
    // LocalDate fimCalendario =
    // primeiroDiaMes.plusMonths(1).withDayOfMonth(1).minusDays(1);
    // fimCalendario = fimCalendario.with(DayOfWeek.SATURDAY);

    // List<CalendarDay> semana = new ArrayList<>();
    // for (LocalDate data = inicioCalendario; !data.isAfter(fimCalendario); data =
    // data.plusDays(1)) {
    // boolean isSelected = data.equals(referencia);
    // CalendarDay dia = new CalendarDay(
    // data.format(DateTimeFormatter.ISO_LOCAL_DATE),
    // String.valueOf(data.getDayOfMonth()),
    // isSelected);
    // semana.add(dia);

    // if (semana.size() == 7) {
    // weeks.add(semana);
    // semana = new ArrayList<>();
    // }
    // }

    // return weeks;
    // }
    public static List<List<Map<String, Object>>> generateCalendar(LocalDate referenceDate) {
        List<List<Map<String, Object>>> weeks = new ArrayList<>();
        LocalDate firstDay = referenceDate.withDayOfMonth(1);
        LocalDate lastDay = referenceDate.withDayOfMonth(referenceDate.lengthOfMonth());

        // Garante que começa na segunda e termina no domingo
        LocalDate start = firstDay.with(java.time.DayOfWeek.MONDAY);
        LocalDate end = lastDay.with(java.time.DayOfWeek.SUNDAY);

        LocalDate current = start;
        List<Map<String, Object>> week = new ArrayList<>();

        while (!current.isAfter(end)) {
            Map<String, Object> day = new HashMap<>();
            day.put("day", current.getDayOfMonth());
            day.put("date", current.toString());
            day.put("selected", current.equals(referenceDate));
            week.add(day);

            if (current.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
                weeks.add(week);
                week = new ArrayList<>();
            }
            current = current.plusDays(1);
        }
        if (!week.isEmpty()) {
            weeks.add(week);
        }
        return weeks;
    }

}
