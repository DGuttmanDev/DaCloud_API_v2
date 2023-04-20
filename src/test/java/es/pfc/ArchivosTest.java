package es.pfc;

import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
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

        MockMultipartFile file1 = new MockMultipartFile("files", "archivo_test_1.txt", "text/plain", "Archivo de prueba 1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "archivo_test_2.txt", "text/plain", "Archivo de prueba 2".getBytes());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file/upload")
                        .file(file1)
                        .file(file2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();

        JsonPath jsonPath = JsonPath.compile("$[*].nombreArchivo");
        List<String> nombresArchivos = jsonPath.read(responseJson);
        Assert.assertEquals(2, nombresArchivos.size());
        Assert.assertTrue(nombresArchivos.contains("archivo_test_1.txt"));
        Assert.assertTrue(nombresArchivos.contains("archivo_test_2.txt"));
    }

}