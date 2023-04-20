package es.pfc;

import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.DependsOn;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ArchivosTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUploadFiles() throws Exception {

        // Crear archivos de prueba
        MockMultipartFile file1 = new MockMultipartFile("files", "archivo_test_1.txt", "text/plain", "Archivo de prueba 1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "archivo_test_2.txt", "text/plain", "Archivo de prueba 2".getBytes());

        // Realizar la solicitud de carga de archivos
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file/upload")
                        .file(file1)
                        .file(file2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Obtener la respuesta como String
        String responseJson = result.getResponse().getContentAsString();

        // Leer los nombres de archivos de la respuesta
        JsonPath jsonPath = JsonPath.compile("$.archivosGuardados[*].nombreArchivo");
        List<String> nombresArchivosGuardados = jsonPath.read(responseJson);

        jsonPath = JsonPath.compile("$.archivosExistentes[*].nombreArchivo");
        List<String> nombresArchivosExistentes = jsonPath.read(responseJson);

        // Verificar que se devuelven los archivos guardados y existentes
        Assert.assertEquals(2, nombresArchivosGuardados.size());
        Assert.assertTrue(nombresArchivosGuardados.contains("archivo_test_1.txt"));
        Assert.assertTrue(nombresArchivosGuardados.contains("archivo_test_2.txt"));

        Assert.assertEquals(0, nombresArchivosExistentes.size());

    }

}