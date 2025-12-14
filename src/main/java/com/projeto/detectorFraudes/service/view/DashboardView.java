package com.projeto.detectorFraudes.service.view;

import com.projeto.detectorFraudes.dto.SuspeitoDTO;
import com.projeto.detectorFraudes.dto.AlertaFinanceiroDTO; // Importe o novo DTO
import com.projeto.detectorFraudes.service.FraudeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3; // Para subtítulos
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class DashboardView extends VerticalLayout {

    public DashboardView(FraudeService service) {
        setSizeFull();
        setPadding(true);
        setAlignItems(Alignment.CENTER);

        add(new H1("🕵️ Painel de Inteligência Antifraude"));

        // --- TABELA 1: MONITORAMENTO DE DEVICES (HARDWARE) ---
        add(new H3("📱 Alertas de Hardware (Device Farms)"));

        Grid<SuspeitoDTO> gridDevices = new Grid<>(SuspeitoDTO.class, false);
        gridDevices.addColumn(SuspeitoDTO::deviceId).setHeader("ID do Dispositivo").setAutoWidth(true);
        gridDevices.addColumn(SuspeitoDTO::qtdUsuarios).setHeader("Usuários Conectados");

        gridDevices.addComponentColumn(item -> {
            Span badge = new Span(item.risco());
            if ("CRÍTICO".equals(item.risco())) badge.getElement().getThemeList().add("badge error");
            else if ("ALTO".equals(item.risco())) badge.getElement().getThemeList().add("badge warning");
            else badge.getElement().getThemeList().add("badge success");
            return badge;
        }).setHeader("Risco");

        gridDevices.addComponentColumn(item -> {
            Button btn = new Button("Bloquear Device");
            btn.getStyle().set("background-color", "#ff4d4d").set("color", "white");
            btn.addClickListener(ev -> {
                service.bloquearDispositivo(item.deviceId());
                btn.setText("Bloqueado");
                btn.setEnabled(false);
            });
            return btn;
        }).setHeader("Ação");

        gridDevices.setItems(service.listarTudo());
        gridDevices.setMaxWidth("1000px");
        gridDevices.setHeight("300px"); // Altura fixa para caber na tela
        add(gridDevices);


        // --- TABELA 2: RASTREAMENTO FINANCEIRO (NOVA!) ---
        add(new H3("💸 Alertas Financeiros (Lavagem de Dinheiro)"));

        Grid<AlertaFinanceiroDTO> gridFinanceiro = new Grid<>(AlertaFinanceiroDTO.class, false);
        gridFinanceiro.addColumn(AlertaFinanceiroDTO::origem).setHeader("Origem (Grupo)").setAutoWidth(true);
        gridFinanceiro.addColumn(AlertaFinanceiroDTO::destino).setHeader("Conta Laranja (Destino)");
        gridFinanceiro.addColumn(item -> "R$ " + item.valor()).setHeader("Total Desviado");
        gridFinanceiro.addColumn(AlertaFinanceiroDTO::motivo).setHeader("Análise da IA").setAutoWidth(true);

        // Carrega os dados da fraude híbrida
        gridFinanceiro.setItems(service.rastrearFraudeHibrida());

        gridFinanceiro.setMaxWidth("1000px");
        add(gridFinanceiro);
    }
}