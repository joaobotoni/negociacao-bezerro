package com.omni.negociacaobezerros.di.data;

import com.omni.negociacaobezerros.data.repositories.synchronizable.CapacidadeFreteRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.CategoriaFreteRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.CategoriaNegociacaoRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.CorretorRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.EmpresaRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.FreteRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.NegociacaoAnimalRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.NegociacaoGadoRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.RacaRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.TipoReferenciaRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.TipoVeiculoFreteRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.ValorReferenciaRepository;
import com.omni.negociacaobezerros.data.repositories.core.contract.Syncable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoSet;
import dagger.multibindings.Multibinds;

@Module
@InstallIn(SingletonComponent.class)
public abstract class SynchronizationModule {
    @Multibinds
    abstract Set<Syncable> syncables();

    @Provides
    static List<Syncable> provideSyncables(Set<Syncable> syncables) {
        return new ArrayList<>(syncables);
    }

    @Binds
    @IntoSet
    abstract Syncable capacidadeFrete(CapacidadeFreteRepository repository);

    @Binds
    @IntoSet
    abstract Syncable categoriaFrete(CategoriaFreteRepository repository);

    @Binds
    @IntoSet
    abstract Syncable categoriaNegociacao(CategoriaNegociacaoRepository repository);

    @Binds
    @IntoSet
    abstract Syncable corretor(CorretorRepository repository);

    @Binds
    @IntoSet
    abstract Syncable empresa(EmpresaRepository repository);

    @Binds
    @IntoSet
    abstract Syncable frete(FreteRepository repository);

    @Binds
    @IntoSet
    abstract Syncable negociacaoAnimal(NegociacaoAnimalRepository repository);

    @Binds
    @IntoSet
    abstract Syncable valorReferencia(ValorReferenciaRepository repository);

    @Binds
    @IntoSet
    abstract Syncable tipoReferencia(TipoReferenciaRepository repository);

    @Binds
    @IntoSet
    abstract Syncable tipoVeiculoFrete(TipoVeiculoFreteRepository repository);

    @Binds
    @IntoSet
    abstract Syncable raca(RacaRepository repository);

    @Binds
    @IntoSet
    abstract Syncable negociacaoGado(NegociacaoGadoRepository repository);
}
