package com.omni.negociacaobezerros.data.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.omni.negociacaobezerros.data.models.Transporte;
import com.omni.negociacaobezerros.data.source.local.dao.CapacidadeFreteDao;
import com.omni.negociacaobezerros.data.source.local.dao.TipoVeiculoFreteDao;
import com.omni.negociacaobezerros.data.source.local.entities.CapacidadeFrete;
import com.omni.negociacaobezerros.data.source.local.entities.TipoVeiculoFrete;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TransporteRepositoryTest {

    private static final long ID_CATEGORIA = 1L;
    private static final long ID_VEICULO = 7L;

    @Test(timeout = 5000)
    public void recomendarTransportes_comQtdeFinalZero_naoTravaNemDividePorZero() {
        CapacidadeFrete invalida = new CapacidadeFrete(1, (int) ID_CATEGORIA, (int) ID_VEICULO, 5, 0);
        FakeCapacidadeFreteDao capacidadeDao = new FakeCapacidadeFreteDao(Collections.singletonList(invalida));
        FakeTipoVeiculoFreteDao tipoVeiculoDao = new FakeTipoVeiculoFreteDao(veiculo("Truck"));
        TransporteRepository repository = new TransporteRepository(capacidadeDao, tipoVeiculoDao);

        List<Transporte> transportes = repository.recomendarTransportes(ID_CATEGORIA, 10);

        assertTrue("capacidade com qtdeFinal == 0 deve ser ignorada", transportes.isEmpty());
    }

    @Test
    public void recomendarTransportes_comCapacidadeValida_distribuiAnimais() {
        CapacidadeFrete valida = new CapacidadeFrete(1, (int) ID_CATEGORIA, (int) ID_VEICULO, 5, 10);
        FakeCapacidadeFreteDao capacidadeDao = new FakeCapacidadeFreteDao(Collections.singletonList(valida));
        FakeTipoVeiculoFreteDao tipoVeiculoDao = new FakeTipoVeiculoFreteDao(veiculo("Truck"));
        TransporteRepository repository = new TransporteRepository(capacidadeDao, tipoVeiculoDao);

        List<Transporte> transportes = repository.recomendarTransportes(ID_CATEGORIA, 20);

        assertEquals(1, transportes.size());
        Transporte transporte = transportes.get(0);
        assertEquals(ID_VEICULO, transporte.getId());
        assertEquals("Truck", transporte.getNomeVeiculo());
        assertEquals(2, transporte.getQuantidade());
        assertEquals(100, transporte.getOcupacao());
    }

    private static TipoVeiculoFrete veiculo(String descricao) {
        TipoVeiculoFrete veiculo = new TipoVeiculoFrete(descricao);
        veiculo.setId((int) ID_VEICULO);
        return veiculo;
    }

    /** Fake DAO: só os métodos usados por TransporteRepository têm comportamento. */
    private static final class FakeCapacidadeFreteDao implements CapacidadeFreteDao {
        private final List<CapacidadeFrete> capacidades;

        FakeCapacidadeFreteDao(List<CapacidadeFrete> capacidades) {
            this.capacidades = capacidades;
        }

        @Override
        public List<CapacidadeFrete> findByCategoria(long idCategoriaFrete) {
            return new ArrayList<>(capacidades);
        }

        @Override
        public List<CapacidadeFrete> getAll() {
            throw new UnsupportedOperationException();
        }

        @Override
        public CapacidadeFrete findById(long id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long insert(CapacidadeFrete capacidadeFrete) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void insertAll(List<CapacidadeFrete> capacidades) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int update(CapacidadeFrete capacidadeFrete) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int delete(CapacidadeFrete capacidadeFrete) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteAll() {
            throw new UnsupportedOperationException();
        }
    }

    /** Fake DAO: só findById tem comportamento. */
    private static final class FakeTipoVeiculoFreteDao implements TipoVeiculoFreteDao {
        private final TipoVeiculoFrete veiculo;

        FakeTipoVeiculoFreteDao(TipoVeiculoFrete veiculo) {
            this.veiculo = veiculo;
        }

        @Override
        public TipoVeiculoFrete findById(long id) {
            return veiculo;
        }

        @Override
        public List<TipoVeiculoFrete> getAll() {
            return Arrays.asList(veiculo);
        }

        @Override
        public long insert(TipoVeiculoFrete tipoVeiculo) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void insertAll(List<TipoVeiculoFrete> tiposVeiculo) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int update(TipoVeiculoFrete tipoVeiculo) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int delete(TipoVeiculoFrete tipoVeiculo) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteAll() {
            throw new UnsupportedOperationException();
        }
    }
}
