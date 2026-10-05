package app.web;

import app.db.Database;
import app.service.ConsultaResultado;
import app.service.ConsultaService;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class WebQueryService {
    public List<Map<String, Object>> executar(String tabela, String modo, String consulta, String parametro) throws Exception {
        try (Connection connection = Database.conectar()) {
            ConsultaResultado resultado = new ConsultaService(connection).executar(tabela, modo, consulta, parametro == null ? "" : parametro);
            List<Map<String, Object>> rows = new ArrayList<>();
            for (Object[] line : resultado.getLinhas()) {
                Map<String, Object> row = new LinkedHashMap<>();
                String[] columns = resultado.getColunas();
                for (int index = 0; index < columns.length; index++) row.put(columns[index], line[index]);
                rows.add(row);
            }
            return rows;
        }
    }
}