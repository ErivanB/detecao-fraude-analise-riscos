package com.projeto.detectorFraudes.service.view; // Verifique se o pacote está certo (talvez seja com.projeto.detectorFraudes.view)

import com.projeto.detectorFraudes.dto.SuspeitoDTO;
import com.projeto.detectorFraudes.service.FraudeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class DashboardView extends VerticalLayout {

    public DashboardView(FraudeService service) {
        setSizeFull();
        setPadding(true);
        setAlignItems(Alignment.CENTER);

        add(new H1("🚨 Monitor de Fraudes (Geral)"));

        Grid<SuspeitoDTO> grid = new Grid<>(SuspeitoDTO.class, false);

        // 1. Colunas Básicas
        grid.addColumn(SuspeitoDTO::deviceId).setHeader("ID do Dispositivo").setAutoWidth(true);
        grid.addColumn(SuspeitoDTO::qtdUsuarios).setHeader("Qtd. Usuários").setSortable(true);

        // 2. Coluna de Risco (Com VERDE, AMARELO e VERMELHO)
        grid.addComponentColumn(item -> {
            Span badge = new Span(item.risco());

            // Lógica de cores baseada no texto do Risco
            if ("CRÍTICO".equals(item.risco())) {
                badge.getElement().getThemeList().add("badge error");   // Vermelho
            } else if ("ALTO".equals(item.risco())) {
                badge.getElement().getThemeList().add("badge warning"); // Amarelo
            } else {
                badge.getElement().getThemeList().add("badge success"); // Verde (Novo!)
            }

            return badge;
        }).setHeader("Risco");

        // 3. Coluna de Ação (Botão Bloquear)
        grid.addComponentColumn(item -> {
            Button btn = new Button("Bloquear");

            // Estilo Vermelho para o botão
            btn.getStyle().set("background-color", "#ff4d4d");
            btn.getStyle().set("color", "white");

            btn.addClickListener(event -> {
                service.bloquearDispositivo(item.deviceId());
                btn.setText("Bloqueado!");
                btn.setEnabled(false);
            });

            return btn;
        }).setHeader("Ação");

        // 4. Carrega TODOS os dados (incluindo os Normais)
        // Certifique-se que você criou o método 'listarTudo' no FraudeService!
        grid.setItems(service.listarTudo());

        grid.setMaxWidth("1000px");
        add(grid);
    }
}