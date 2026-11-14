import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useNavigate } from 'react-router'
import { Mail, User } from 'lucide-react'
import AuthLayout from '../components/AuthLayout'
import CampoFormulario from '../components/CampoFormulario'
import CampoSenha from '../components/CampoSenha'
import AlertaDeErro from '../components/AlertaDeErro'
import BotaoEnviar from '../components/BotaoEnviar'
import { classeInput } from '../components/estilos'
import { useAuth } from '../contexts/useAuth'
import { mensagemDeErro } from '../services/erros'

const schema = z.object({
  nome: z.string().trim().min(1, 'Nome é obrigatório'),
  email: z.string().min(1, 'E-mail é obrigatório').email('E-mail inválido'),
  senha: z
    .string()
    .min(8, 'A senha deve ter no mínimo 8 caracteres')
    .regex(/[A-Z]/, 'A senha deve ter ao menos 1 letra maiúscula')
    .regex(/\d/, 'A senha deve ter ao menos 1 número'),
})

type DadosDoRegistro = z.infer<typeof schema>

export default function Registro() {
  const { registrar } = useAuth()
  const navigate = useNavigate()
  const [erroDoServidor, setErroDoServidor] = useState('')

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<DadosDoRegistro>({ resolver: zodResolver(schema) })

  async function aoEnviar(dados: DadosDoRegistro) {
    setErroDoServidor('')
    try {
      await registrar(dados.nome, dados.email, dados.senha)
      navigate('/dashboard', { replace: true })
    } catch (erro) {
      setErroDoServidor(mensagemDeErro(erro))
    }
  }

  return (
    <AuthLayout>
      {erroDoServidor && <AlertaDeErro mensagem={erroDoServidor} />}

      <form onSubmit={handleSubmit(aoEnviar)} noValidate className="space-y-4">
        <CampoFormulario id="nome" rotulo="Nome Completo" icone={User} erro={errors.nome?.message}>
          <input
            id="nome"
            type="text"
            autoComplete="name"
            placeholder="Seu nome"
            className={classeInput}
            {...register('nome')}
          />
        </CampoFormulario>

        <CampoFormulario id="email" rotulo="E-mail" icone={Mail} erro={errors.email?.message}>
          <input
            id="email"
            type="email"
            autoComplete="email"
            placeholder="seu.email@exemplo.com"
            className={classeInput}
            {...register('email')}
          />
        </CampoFormulario>

        <CampoSenha
          registro={register('senha')}
          erro={errors.senha?.message}
          autoComplete="new-password"
        />

        <BotaoEnviar carregando={isSubmitting} texto="Criar Conta Grátis" />
      </form>
    </AuthLayout>
  )
}