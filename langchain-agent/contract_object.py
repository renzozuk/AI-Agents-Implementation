from typing import List, Optional
from pydantic import BaseModel, Field


class Empresa(BaseModel):
    razaoSocial: Optional[str] = None
    nomeFantasia: Optional[str] = None
    cnpj: Optional[str] = None
    telefone: Optional[str] = None
    email: Optional[str] = None
    responsavel: Optional[str] = None
    logradouro: Optional[str] = None
    numero: Optional[str] = None
    complemento: Optional[str] = None
    bairro: Optional[str] = None
    cep: Optional[str] = None
    cidade: Optional[str] = None
    uf: Optional[str] = None
    observacoes: Optional[str] = None


class VagaItem(BaseModel):
    funcaoId: Optional[str] = None
    quantidade: Optional[int] = None
    remuneracaoCentavos: Optional[int] = None
    descricaoFuncoes: Optional[str] = None


class VagasWrapper(BaseModel):
    vagas: List[VagaItem] = []


class ContratoModel(BaseModel):
    empresa: Optional[Empresa] = None
    vagas: Optional[VagasWrapper] = None
    orgaoId: Optional[str] = None
    valorCentavosContrato: Optional[int] = None
    dataInicioVigencia: Optional[str] = None
    dataFimVigencia: Optional[str] = None
    localTrabalho: Optional[str] = None
    tipoTrabalho: Optional[str] = None
    cargaHoraria: Optional[str] = None
    objetoContrato: Optional[str] = None


class FullExtractionSchema(BaseModel):
    pdf_title: Optional[str] = None
    page_count: Optional[int] = None
    contrato: Optional[ContratoModel] = None
    status_extracao: Optional[str] = Field(
        default="INCOMPLETE",
        description="'COMPLETE' or 'INCOMPLETE'"
    )
